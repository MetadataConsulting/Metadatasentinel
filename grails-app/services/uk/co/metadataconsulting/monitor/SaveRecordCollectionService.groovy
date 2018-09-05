package uk.co.metadataconsulting.monitor


import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import uk.co.metadataconsulting.monitor.modelcatalogue.DataModel

@Slf4j
@CompileStatic
/**
 * Service to save (create new) a RecordCollection and populate it with Records from an file (InputStream) using a tabular data import service,
 * given a RecordFileCommand from a controller.
 */
class SaveRecordCollectionService {

    CsvImportService csvImportService

    ExcelImportService excelImportService

    RuleFetcherService ruleFetcherService

    RecordCollectionGormService recordCollectionGormService

    UploadFileService uploadFileService

    @CompileDynamic
    /**
     * Create a new RecordCollection and populate it with Records from an file (InputStream) using the CSV import service.
     */
    RecordCollectionGormEntity save(RecordFileCommand cmd) {
        List<DataModel> dataModelList = ruleFetcherService.fetchDataModels()?.dataModels
        DataModel dataModel = dataModelList?.find { it.id == cmd.dataModelId }
        if (!dataModel) {
            log.warn 'data model not found with Id: {}', cmd.dataModelId
            return null
        }

        final InputStream inputStream = cmd.csvFile.inputStream
        final Integer batchSize = cmd.batchSize

        RecordCollectionGormEntity recordCollectionEntity = recordCollectionGormService.save(cmd)

        final Long recordCollectionEntityId = recordCollectionEntity.id

        recordCollectionGormService.associateWithDataModel(recordCollectionEntity, dataModel)

        TabularDataImportService tabularDataImportService = csvImportByContentType(ImportContentType.of(cmd.csvFile.contentType))
        tabularDataImportService.save(inputStream, batchSize, recordCollectionEntity)

        UploadFileResult uploadFileResult = uploadFileService.uploadFile(recordCollectionEntityId, cmd.csvFile)
        if (uploadFileResult != null) {
            RecordCollectionGormEntity.withNewSession {
                recordCollectionGormService.updateFileMetadata(recordCollectionEntityId, uploadFileResult)
            }
        }

        recordCollectionEntity
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
