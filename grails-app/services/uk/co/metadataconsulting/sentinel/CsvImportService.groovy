package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.hibernate.SessionFactory
import org.hibernate.Session
import org.springframework.context.MessageSource
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules

@Slf4j
@CompileStatic
class CsvImportService implements CsvImport, Benchmark {

    ValidateRecordPortionService validateRecordPortionService

    CsvImportProcessorService csvImportProcessorService

    RuleFetcherService ruleFetcherService

    RecordGormService recordGormService

    RecordCollectionGormService recordCollectionGormService

    SessionFactory sessionFactory

    def executorService

    @CompileDynamic
    @Override
    void save(List<String> gormUrls, InputStream inputStream, Integer batchSize) {
        RecordCollectionGormEntity recordCollection = recordCollectionGormService.save()

        executorService.submit {
            log.info 'fetching validation rules'
            Map<String, ValidationRules> gormUrlsRules = ruleFetcherService.fetchValidationRules(gormUrls)
            long duration = benchmark {
                log.info 'processing input stream'
                csvImportProcessorService.processInputStream(inputStream, batchSize) { List<List<String>> valuesList ->
                    save(recordCollection, valuesList, gormUrls, gormUrlsRules)
                    cleanUpGorm()
                }
            }
            log.info "execution batchSize: ${batchSize} took ${duration} ms"
        }
    }

    void save(RecordCollectionGormEntity recordCollection, List<List<String>> valuesList, List<String> gormUrls, Map<String, ValidationRules> gormUrlsRules) {
        for ( List<String> values : valuesList ) {
            List<RecordPortion> recordPortionList = recordPortionListOfValue(values, gormUrls, gormUrlsRules)
            recordGormService.save(recordCollection, recordPortionList)
        }
    }

    List<RecordPortion> recordPortionListOfValue(List<String> values, List<String> gormUrls, Map<String, ValidationRules> gormUrlsRules) {
        List<RecordPortion> recordPortionList = []

        for ( int i = 0; i < values.size(); i++ ) {
            String value = values[i]
            String gormUrl = gormUrls[i]
            recordPortionList << recordPortionFromValue(gormUrl, value, gormUrls, values, gormUrlsRules)
        }
        recordPortionList
    }

    RecordPortion recordPortionFromValue(String gormUrl, String value, List<String> gormUrls, List<String> values, Map<String, ValidationRules> gormUrlsRules) {
        ValidationRules validationRules = gormUrlsRules[gormUrl]

        if ( validationRules ) {
            String reason = validateRecordPortionService.failureReason(validationRules, gormUrls, values)
            String name = validationRules.name
            return new RecordPortion(name: name,
                    gormUrl: gormUrl,
                    value: value,
                    valid: !(reason as boolean),
                    reason: reason,
                    numberOfRulesValidatedAgainst: validationRules.rules?.size() ?: 0)
        }
        new RecordPortion(gormUrl: gormUrl, value: value, valid: true, numberOfRulesValidatedAgainst: 0)
    }

    def cleanUpGorm() {
        Session session = sessionFactory.currentSession
        session.flush()
        session.clear()
    }
}