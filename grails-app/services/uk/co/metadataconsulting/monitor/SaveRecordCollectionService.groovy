package uk.co.metadataconsulting.monitor

import grails.async.Promise
import grails.gorm.transactions.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.transaction.annotation.Propagation
import uk.co.metadataconsulting.monitor.modelcatalogue.DataModel
import uk.co.metadataconsulting.monitor.validationTask.ValidationTaskGormService

import static grails.async.Promises.task

/**
 * Service to save (create new) a RecordCollection and populate it with Records from an file (InputStream) using a tabular data import service,
 * given a RecordFileCommand from a controller.
 */
@Slf4j
@CompileStatic
class SaveRecordCollectionService {

    CsvImportService csvImportService

    ExcelImportService excelImportService

    RuleFetcherService ruleFetcherService

    RecordCollectionGormService recordCollectionGormService

    ValidationTaskGormService validationTaskGormService

    UploadFileService uploadFileService

    @Transactional
    Long saveWithValidationTask(RecordFileCommand cmd, Long validationTaskId) {
        RecordCollectionGormEntity recordCollectionGormEntity = save(cmd)
        if (validationTaskId) {
            validationTaskGormService.update(validationTaskId, recordCollectionGormEntity)
        } else {
            validationTaskGormService.save("${recordCollectionGormEntity.datasetName} Validation Task", recordCollectionGormEntity)
        }
        recordCollectionGormEntity.id
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void uploadFileToRecordCollection(Long recordCollectionEntityId, RecordFileCommand cmd) {
        try {
            log.info 'uploading File'
            RecordCollectionGormEntity recordCollectionEntity = recordCollectionGormService.find(recordCollectionEntityId)
            if (recordCollectionEntity) {
                UploadFileResult uploadFileResult = uploadFileService.uploadFile(recordCollectionEntityId, cmd.csvFile)
                if (uploadFileResult) {
                    recordCollectionEntity.fileUrl = uploadFileResult.fileUrl
                    recordCollectionEntity.fileKey = uploadFileResult.path
                    recordCollectionGormService.save(recordCollectionEntity)
                } else {
                    log.warn("file upload failed")
                }
            } else {
                log.warn("record collection not found by id {}", recordCollectionEntityId)
            }
            log.info 'upload file finished'
        } catch(Throwable t) {
            log.error('error while uploading file', t)
        }
    }

    /**
     * Create a new RecordCollection and populate it with Records from an file (InputStream) using the CSV import service.
     */
    @Transactional
    @CompileDynamic
    RecordCollectionGormEntity save(RecordFileCommand cmd) {
        List<DataModel> dataModelList = ruleFetcherService.fetchDataModels()?.dataModels
        DataModel dataModel = dataModelList?.find { it.id == cmd.dataModelId }
        if (!dataModel) {
            log.warn 'data model not found with Id: {}', cmd.dataModelId
            return null
        }
        RecordCollectionGormEntity recordCollectionEntity = recordCollectionGormService.save(cmd)
        recordCollectionGormService.associateWithDataModel(recordCollectionEntity, dataModel)
        recordCollectionEntity
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void importFileContents(Long recordCollectionId, RecordFileCommand cmd) {
        RecordCollectionGormEntity recordCollectionEntity = recordCollectionGormService.find(recordCollectionId)
        final InputStream inputStream = cmd.csvFile.inputStream
        final Integer batchSize = cmd.batchSize
        TabularDataImportService tabularDataImportService = csvImportByContentType(ImportContentType.of(cmd.csvFile.contentType))
        tabularDataImportService.save(inputStream, batchSize, recordCollectionEntity)

    }

    protected TabularDataImportService csvImportByContentType(ImportContentType contentType) {
        if ( contentType == ImportContentType.XSLX ) {
            return excelImportService
        }
        if ( contentType == ImportContentType.CSV ) {
            return csvImportService
        }
        null
    }
}
