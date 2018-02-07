package uk.co.metadataconsulting.sentinel

import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.hibernate.SessionFactory
import org.hibernate.Session
import org.springframework.context.MessageSource

@Slf4j
@CompileStatic
class CsvImportGormService implements CsvImportService, Benchmark {

    CsvImportProcessorService csvImportProcessorService

    RecordGormService recordGormService

    RecordCollectionGormService recordCollectionGormService

    MessageSource messageSource

    SessionFactory sessionFactory

    @Override
    @Transactional
    void save(Long recordCollectionId, String mapping, InputStream inputStream, Integer batchSize) {
        RecordCollectionGormEntity recordCollection = recordCollectionGormService.find(recordCollectionId)
        long duration = benchmark {
            csvImportProcessorService.processInputStream(inputStream, batchSize) { List<List<String>> valuesList ->
                save(recordCollection, valuesList, mapping)
                cleanUpGorm()
            }
        }
        log.info "execution batchSize: ${batchSize} took ${duration} ms"
    }

    @Transactional
    void save(RecordCollectionGormEntity recordCollection, List<List<String>> valuesList, String mapping) {

        for ( List<String> values : valuesList ) {
            List<RecordPortion> recordPortionList = values.collect { String value ->
                String metadataDomainEntity = 'XXX'
                Boolean valid = true
                new RecordPortion(metadataDomainEntity: metadataDomainEntity, value: value, valid: valid)
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