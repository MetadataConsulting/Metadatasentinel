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
class CsvImportService implements TabularDataImportService, Benchmark {

    CsvImportProcessorService csvImportProcessorService

    RecordCollectionGormService recordCollectionGormService

    ReconciliationService reconciliationService

    CatalogueElementsService catalogueElementsService

    ImportService importService

    SessionFactory sessionFactory
    
    @CompileDynamic
    @Override
    /**
     * Save a CSV file as the Records of a given (should be newly created) RecordCollection. A preliminary "default" mapping is made here.
     */
    void save(InputStream inputStream,
              Integer batchSize,
              RecordCollectionGormEntity recordCollectionEntity) {

        Promise p = task {
            RecordCollectionGormEntity.withNewSession {
                log.info 'fetching validation rules'
                MappingMetadata metadata = new MappingMetadata()
                Closure headerListClosure = { List<String> l ->
                    metadata.setHeadersList(l)
                    Map<String, List<GormUrlName>> suggestions = [:]
                    log.info '#dataModel {}', recordCollectionEntity.dataModelId

                    if (recordCollectionEntity.dataModelId) {
                        List<GormUrlName> calogueElements = catalogueElementsService.findAllByDataModelId(recordCollectionEntity.dataModelId)
                        log.info '#headers {} #catalogueElements {}', l.size(), calogueElements.size()
                        suggestions = reconciliationService.reconcile(calogueElements, l)
                    }
                    recordCollectionGormService.saveRecordCollectionMappingWithHeaders(recordCollectionEntity, l, suggestions)
                }

                log.info 'processing input stream'
                csvImportProcessorService.processInputStream(inputStream, batchSize, headerListClosure) { List<List<String>> valuesList ->
                    log.info('inside closure block')
                    importService.saveMatrixOfValuesToRecordCollection(recordCollectionEntity, valuesList, metadata)
                    cleanUpGorm()
                }
            }
        }
        p.onComplete {
            log.info 'excel import finished'
        }
        p.onError { Throwable t ->
            log.error 'error while importing csv', t.message
        }
    }


    def cleanUpGorm() {
        Session session = sessionFactory.currentSession
        session.flush()
        session.clear()
    }
}

