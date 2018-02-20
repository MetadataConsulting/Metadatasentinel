package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.hibernate.SessionFactory
import org.hibernate.Session
import org.modelcatalogue.core.scripting.Validating
import org.modelcatalogue.core.scripting.ValueValidator
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
            MappingMetadata metadata = new MappingMetadata(gormUrls: gormUrls, gormUrlsRules: gormUrlsRules)
            Closure headerListClosure = { List<String> l ->
                metadata.setHeaderLineList(l)
            }
            log.info 'processing input stream'
            csvImportProcessorService.processInputStream(inputStream, batchSize, headerListClosure) { List<List<String>> valuesList ->
                save(recordCollection, valuesList, metadata)
                cleanUpGorm()
            }
        }
    }

    void save(RecordCollectionGormEntity recordCollection, List<List<String>> valuesList, MappingMetadata metadata) {
        for ( List<String> values : valuesList ) {
            List<RecordPortion> recordPortionList = recordPortionListOfValue(values, metadata)
            recordGormService.save(recordCollection, recordPortionList)
        }
    }

    List<RecordPortion> recordPortionListOfValue(List<String> values, MappingMetadata metadata) {
        List<RecordPortion> recordPortionList = []

        for ( int i = 0; i < values.size(); i++ ) {
            String value = values[i]
            String header = metadata.headerLineList[i]
            String gormUrl = metadata.gormUrls[i]
            recordPortionList << recordPortionFromValue(gormUrl, value, header, values, metadata)
        }
        recordPortionList
    }

    RecordPortion recordPortionFromValue(String gormUrl, String value, String header, List<String> values, MappingMetadata metadata) {
        ValidationRules validationRules = metadata.gormUrlsRules[gormUrl]

        String reason
        String name = header
        int numberOfRulesValidatedAgainst = 0

        if ( validationRules ) {
            reason = validateRecordPortionService.failureReason(validationRules, metadata.gormUrls, values)
            name = validationRules.name ?: header
            numberOfRulesValidatedAgainst = validationRules.rules?.size() ?: 0

            if ( validationRules.validating ) {
                if ( !ValueValidator.validateRule(validationRules.validating, value) ) {
                    reason = reason ?: validationRules.validating.toString()
                }
                numberOfRulesValidatedAgainst++
            }
        }

        new RecordPortion(name: name,
                gormUrl: gormUrl,
                value: value,
                valid: !(reason as boolean),
                reason: reason,
                numberOfRulesValidatedAgainst: numberOfRulesValidatedAgainst)
    }

    def cleanUpGorm() {
        Session session = sessionFactory.currentSession
        session.flush()
        session.clear()
    }
}

class MappingMetadata {
    List<String> headerLineList
    List<String> gormUrls
    Map<String, ValidationRules> gormUrlsRules
}