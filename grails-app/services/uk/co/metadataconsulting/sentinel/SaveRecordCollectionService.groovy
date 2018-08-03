package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import uk.co.metadataconsulting.sentinel.modelcatalogue.DataModel

@Slf4j
@CompileStatic
class SaveRecordCollectionService {

    CsvImportService csvImportService

    ExcelImportService excelImportService

    RuleFetcherService ruleFetcherService

    RecordCollectionGormService recordCollectionGormService

    UploadFileService uploadFileService

    def executorService

    @CompileDynamic
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

        recordCollectionGormService.associateWithDataModel(recordCollectionEntity, dataModel)

        CsvImport importService = csvImportByContentType(ImportContentType.of(cmd.csvFile.contentType))
        importService.save(inputStream, batchSize, recordCollectionEntity)

        executorService.submit {
            UploadFileResult uploadFileResult = uploadFileService.uploadFile(recordCollectionEntity.id, cmd.csvFile)
            recordCollectionGormService.updateFileMetadata(recordCollectionEntity.id, uploadFileResult)
        }

        recordCollectionEntity
    }

    protected CsvImport csvImportByContentType(ImportContentType contentType) {
        if ( contentType == ImportContentType.XSLX ) {
            return excelImportService
        }
        if ( contentType == ImportContentType.CSV ) {
            return csvImportService
        }
        null
    }
}
