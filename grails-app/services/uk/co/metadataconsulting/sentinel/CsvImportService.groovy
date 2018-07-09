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

    CsvImportProcessorService csvImportProcessorService

    RecordCollectionGormService recordCollectionGormService

    ImportService importService

    SessionFactory sessionFactory

    def executorService
    
    @CompileDynamic
    @Override
    void save(InputStream inputStream, String datasetName, Integer batchSize ) {

        RecordCollectionGormEntity recordCollection = recordCollectionGormService.save(datasetName)

        executorService.submit {
            log.info 'fetching validation rules'
            MappingMetadata metadata = new MappingMetadata()
            Closure headerListClosure = { List<String> l ->
                metadata.setHeaderLineList(l)
                recordCollectionGormService.saveRecordCollectionMappingWithHeaders(recordCollection, l)
            }

            log.info 'processing input stream'
            csvImportProcessorService.processInputStream(inputStream, batchSize, headerListClosure) { List<List<String>> valuesList ->
                log.info ('inside closure block')
                importService.saveListOfValues(recordCollection, valuesList, metadata)
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

