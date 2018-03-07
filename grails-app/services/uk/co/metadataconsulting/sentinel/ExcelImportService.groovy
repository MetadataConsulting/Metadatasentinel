package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.hibernate.Session
import org.hibernate.SessionFactory

@Slf4j
@CompileStatic
class ExcelImportService implements CsvImport, Benchmark {

    RecordCollectionGormService recordCollectionGormService

    ImportService importService

    SessionFactory sessionFactory

    def executorService
    
    @CompileDynamic
    @Override
    void save(List<String> gormUrls, InputStream inputStream, Integer batchSize) {
        RecordCollectionGormEntity recordCollection = recordCollectionGormService.save()

//        executorService.submit {
            log.info 'fetching validation rules'
            MappingMetadata metadata = importService.mappingMetadata(gormUrls)
            Closure headerListClosure = { List<String> l ->
                metadata.setHeaderLineList(l)
            }
            log.info 'processing input stream'
            ExcelReader.read(inputStream, 0, true, headerListClosure) { List<String> values ->
                importService.save(recordCollection, values, metadata)
                cleanUpGorm()
            }
  //      }
    }

    def cleanUpGorm() {
        Session session = sessionFactory.currentSession
        session.flush()
        session.clear()
    }
}

