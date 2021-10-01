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

    RecordCollectionService recordCollectionService

    ImportService importService

    /**
     * Save a CSV file as the Records of a given (should be newly created) RecordCollection. A preliminary "default" mapping is made here.
     */
    @CompileDynamic
    @Override
    void save(InputStream inputStream,
              Integer batchSize,
              RecordCollectionGormEntity recordCollectionEntity) {
        Promise p = task {
                log.info 'fetching validation rules'
                MappingMetadata metadata = new MappingMetadata()

                log.info 'processing input stream'
                csvImportProcessorService.processInputStream(inputStream, batchSize,
    { List<String> headersList ->
                        log.info 'updating headers'
                        metadata.setHeadersList(headersList)
                        recordCollectionGormService.addHeadersList(recordCollectionEntity, headersList)
                }) { List<List<String>> valuesList ->
                    log.info('inside closure block')
                    importService.saveMatrixOfValuesToRecordCollection(recordCollectionEntity, valuesList, metadata)
                }
                recordCollectionService.generateSuggestedMappings(recordCollectionEntity)
        }
        p.onComplete {
            log.info 'csv import finished'
        }
        p.onError { Throwable t ->
            log.error('error while importing csv', t)
        }
    }
}

