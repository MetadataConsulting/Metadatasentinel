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
    void save(InputStream inputStream,
              Integer batchSize,
              RecordCollectionGormEntity recordCollectionEntity) {

        executorService.submit {
            log.info 'fetching validation rules'
            MappingMetadata metadata = new MappingMetadata()
            Closure headerListClosure = { List<String> l ->
                metadata.setHeaderLineList(l)
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
                log.info ('inside closure block')
                importService.saveListOfValues(recordCollectionEntity, valuesList, metadata)
                cleanUpGorm()
            }
        }
    }


    def cleanUpGorm() {
        Session session = sessionFactory.currentSession
        session.flush()
        session.clear()
    }
}

