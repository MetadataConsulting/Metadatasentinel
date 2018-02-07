package uk.co.metadataconsulting.sentinel

import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.hibernate.SessionFactory
import org.hibernate.Session
import org.springframework.context.MessageSource
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRule

@Slf4j
@CompileStatic
class CsvImportService implements CsvImport, Benchmark {

    CsvImportProcessorService csvImportProcessorService

    DlrValidatorService dlrValidatorService

    RuleFetcherService ruleFetcherService

    RecordGormService recordGormService

    RecordCollectionGormService recordCollectionGormService

    MessageSource messageSource

    SessionFactory sessionFactory

    @Override
    @Transactional
    void save(String mapping, InputStream inputStream, Integer batchSize) {
        RecordCollectionGormEntity recordCollection = recordCollectionGormService.save()

        List<String> mappingList = mapping.split(',') as List<String>
        Map<String, List<ValidationRule>> mappingRules = [:]
        for ( String str : mappingList ) {
            mappingRules[str] = ruleFetcherService.fetchValidationRules(str)
        }

        long duration = benchmark {
            csvImportProcessorService.processInputStream(inputStream, batchSize) { List<List<String>> valuesList ->
                save(recordCollection, valuesList, mappingList, mappingRules)
                cleanUpGorm()
            }
        }
        log.info "execution batchSize: ${batchSize} took ${duration} ms"
    }

    @Transactional
    void save(RecordCollectionGormEntity recordCollection, List<List<String>> valuesList, List<String> mappingList, Map<String, List<ValidationRule>> mappingRules) {

        for ( List<String> values : valuesList ) {
            List<RecordPortion> recordPortionList = []

            for ( int i = 0; i < values.size(); i++ ) {
                String value = values[i]
                String mapping = mappingList[i]
                List<ValidationRule> rules = mappingRules[mapping]
                String metadataDomainEntity = 'XXX'

                boolean isValid = true
                for ( ValidationRule validationRule : rules ) {
                    isValid = dlrValidatorService.validate([value] as String[], validationRule.rule)
                    if ( !isValid ) {
                        break
                    }
                }
                String name = mapping // TODO
                recordPortionList << new RecordPortion(name: name, metadataDomainEntity: metadataDomainEntity, value: value, valid: isValid)
            }
            recordGormService.save(recordCollection, recordPortionList)
        }
    }

    def cleanUpGorm() {
        Session session = sessionFactory.currentSession
        session.flush()
        session.clear()
    }
}