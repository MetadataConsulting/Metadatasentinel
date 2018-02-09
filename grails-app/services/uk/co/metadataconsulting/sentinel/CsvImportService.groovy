package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.hibernate.SessionFactory
import org.hibernate.Session
import org.springframework.context.MessageSource
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRule
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules
import static grails.async.Promises.task

@Slf4j
@CompileStatic
class CsvImportService implements CsvImport, Benchmark {

    ValidateRecordPortionService validateRecordPortionService

    CsvImportProcessorService csvImportProcessorService

    RuleFetcherService ruleFetcherService

    RecordGormService recordGormService

    RecordCollectionGormService recordCollectionGormService

    MessageSource messageSource

    SessionFactory sessionFactory

    @Override
    void save(List<String> gormUrls, InputStream inputStream, Integer batchSize) {
        RecordCollectionGormEntity recordCollection = recordCollectionGormService.save()

        //task {
            log.info 'fetching validation rules'
            Map<String, ValidationRules> gormUrlsRules = fetchValidationRules(gormUrls)
            long duration = benchmark {
                log.info 'processing input stream'
                csvImportProcessorService.processInputStream(inputStream, batchSize) { List<List<String>> valuesList ->
                    save(recordCollection, valuesList, gormUrls, gormUrlsRules)
                    cleanUpGorm()
                }
            }
            log.info "execution batchSize: ${batchSize} took ${duration} ms"
        //}
    }

    Map<String, ValidationRules> fetchValidationRules(List<String> gormUrls) {
        Map<String, ValidationRules> gormUrlsRules = [:]
        for ( String gormUrl : gormUrls ) {
            ValidationRules validationRules = ruleFetcherService.fetchValidationRules(gormUrl)
            if ( validationRules ) {
                gormUrlsRules[gormUrl] = validationRules
            }
        }
        gormUrlsRules
    }

    void save(RecordCollectionGormEntity recordCollection, List<List<String>> valuesList, List<String> gormUrls, Map<String, ValidationRules> gormUrlsRules) {

        for ( List<String> values : valuesList ) {
            List<RecordPortion> recordPortionList = []

            for ( int i = 0; i < values.size(); i++ ) {
                String value = values[i]
                String gormUrl = gormUrls[i]
                recordPortionList << recordPortionFromValue(gormUrl, value, gormUrls, values, gormUrlsRules)
            }
            recordGormService.save(recordCollection, recordPortionList)
        }
    }

    RecordPortion recordPortionFromValue(String gormUrl, String value, List<String> gormUrls, List<String> values, Map<String, ValidationRules> gormUrlsRules) {
        ValidationRules validationRules = gormUrlsRules[gormUrl]

        if ( validationRules ) {
            String reason = validateRecordPortionService.failureReason(validationRules, gormUrls, values)
            String name = validationRules.name
            return new RecordPortion(name: name, gormUrl: gormUrl, value: value, valid: !(reason as boolean), reason: reason)
        }
        new RecordPortion(gormUrl: gormUrl, value: value, valid: true)
    }

    def cleanUpGorm() {
        Session session = sessionFactory.currentSession
        session.flush()
        session.clear()
    }
}