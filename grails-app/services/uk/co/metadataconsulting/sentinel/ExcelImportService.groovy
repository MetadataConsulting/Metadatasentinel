package uk.co.metadataconsulting.sentinel

import grails.async.Promise
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.hibernate.Session
import org.hibernate.SessionFactory
import uk.co.metadataconsulting.sentinel.modelcatalogue.GormUrlName

import static grails.async.Promises.task

@Slf4j
@CompileStatic
class ExcelImportService implements CsvImport, Benchmark {

    RecordCollectionGormService recordCollectionGormService

    CatalogueElementsService catalogueElementsService

    ReconciliationService reconciliationService

    ImportService importService

    SessionFactory sessionFactory

    @CompileDynamic
    @Override
    void save(InputStream inputStream,
              Integer batchSize,
              RecordCollectionGormEntity recordCollectionEntity) {

        Promise p = task {
            RecordCollectionGormEntity.withNewSession {
                log.info 'fetching validation rules'
                MappingMetadata metadata = new MappingMetadata()
                Closure headerListClosure = { List<String> l ->
                    metadata.setHeaderLineList(l)
                    Map<String, List<GormUrlName>> suggestions = [:]

                    if (recordCollectionEntity.dataModelId ) {
                        List<GormUrlName> calogueElements = catalogueElementsService.findAllByDataModelId(recordCollectionEntity.dataModelId)
                        suggestions = reconciliationService.reconcile(calogueElements, l)
                    }

                    recordCollectionGormService.saveRecordCollectionMappingWithHeaders(recordCollectionEntity, l, suggestions)

                }
                log.info 'processing input stream'
                ExcelReader.read(inputStream, 0, true, headerListClosure) { List<String> values ->
                    importService.save(recordCollectionEntity, values, metadata)
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

