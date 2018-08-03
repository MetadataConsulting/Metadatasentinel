package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.hibernate.Session
import org.hibernate.SessionFactory
import uk.co.metadataconsulting.sentinel.modelcatalogue.GormUrlName

@Slf4j
@CompileStatic
class ExcelImportService implements CsvImport, Benchmark {

    RecordCollectionGormService recordCollectionGormService

    CatalogueElementsService catalogueElementsService

    ReconciliationService reconciliationService

    ImportService importService

    SessionFactory sessionFactory

    def executorService
    
    @CompileDynamic
    @Override
    RecordCollectionGormEntity save(InputStream inputStream, Integer batchSize, RecordCollectionMetadata recordCollectionMetadata) {
        RecordCollectionGormEntity recordCollection = recordCollectionGormService.save(recordCollectionMetadata)

        executorService.submit {
            log.info 'fetching validation rules'
            MappingMetadata metadata = new MappingMetadata()
            Closure headerListClosure = { List<String> l ->
                metadata.setHeaderLineList(l)
                Map<String, List<GormUrlName>> suggestions = [:]

                if (recordCollection.dataModelId ) {
                    List<GormUrlName> calogueElements = catalogueElementsService.findAllByDataModelId(recordCollection.dataModelId)
                    suggestions = reconciliationService.reconcile(calogueElements, l)
                }

                recordCollectionGormService.saveRecordCollectionMappingWithHeaders(recordCollection, l, suggestions)

            }
            log.info 'processing input stream'
            ExcelReader.read(inputStream, 0, true, headerListClosure) { List<String> values ->
                importService.save(recordCollection, values, metadata)
                cleanUpGorm()
            }
        }
        recordCollection
    }

    def cleanUpGorm() {
        Session session = sessionFactory.currentSession
        session.flush()
        session.clear()
    }
}

