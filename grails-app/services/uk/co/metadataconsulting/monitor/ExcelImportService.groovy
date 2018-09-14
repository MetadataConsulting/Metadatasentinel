package uk.co.metadataconsulting.monitor

import grails.async.Promise
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.hibernate.Session
import org.hibernate.SessionFactory
import uk.co.metadataconsulting.monitor.modelcatalogue.GormUrlName

import static grails.async.Promises.task

@Slf4j
@CompileStatic
class ExcelImportService implements TabularDataImportService, Benchmark {

    RecordCollectionGormService recordCollectionGormService

    CatalogueElementsService catalogueElementsService

    ReconciliationService reconciliationService

    ImportService importService

    SessionFactory sessionFactory

    @CompileDynamic
    @Override
    /**
     * forNewValidationTask unused here.
     */
    void save(InputStream inputStream,
              Integer batchSize,
              RecordCollectionGormEntity recordCollectionEntity,
              Boolean forNewValidationTask = true) {

        Promise p = task {
            RecordCollectionGormEntity.withNewSession {
                log.info 'fetching validation rules'
                MappingMetadata metadata = new MappingMetadata()
                Closure headerListClosure = { List<String> headersList ->
                    metadata.setHeadersList(headersList)
                    recordCollectionGormService.addHeadersList(recordCollectionEntity, headersList)
                }
                log.info 'processing input stream'
                ExcelReader.read(inputStream, 0, true, headerListClosure) { List<String> values ->
                    importService.saveListOfValuesAsRecord(recordCollectionEntity, values, metadata)
                    cleanUpGorm()
                }
            }

        }
        p.onComplete {
            log.info 'excel import finished'
        }
        p.onError { Throwable t ->
            log.error 'error while importing excel', t.message
        }
    }

    def cleanUpGorm() {
        Session session = sessionFactory.currentSession
        session.flush()
        session.clear()
    }
}

