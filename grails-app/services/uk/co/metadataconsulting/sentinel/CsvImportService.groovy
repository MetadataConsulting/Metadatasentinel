package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.hibernate.SessionFactory
import org.hibernate.Session
import org.modelcatalogue.core.scripting.Validating
import org.modelcatalogue.core.scripting.ValueValidator
import org.springframework.context.MessageSource
import uk.co.metadataconsulting.sentinel.modelcatalogue.GormUrlName
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules

@Slf4j
@CompileStatic
class CsvImportService implements CsvImport, Benchmark {

    CsvImportProcessorService csvImportProcessorService

    RecordCollectionGormService recordCollectionGormService

    ReconciliationService reconciliationService

    CatalogueElementsService catalogueElementsService

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
                if (recordCollection.dataModelId) {
                    List<GormUrlName> calogueElements = catalogueElementsService.findAllByDataModelId(recordCollection.dataModelId)
                    suggestions = reconciliationService.reconcile(calogueElements, l)
                }
                recordCollectionGormService.saveRecordCollectionMappingWithHeaders(recordCollection, l, suggestions)
            }

            log.info 'processing input stream'
            csvImportProcessorService.processInputStream(inputStream, batchSize, headerListClosure) { List<List<String>> valuesList ->
                log.info ('inside closure block')
                importService.saveListOfValues(recordCollection, valuesList, metadata)
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

