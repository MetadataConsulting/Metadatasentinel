package uk.co.metadataconsulting.sentinel

import grails.gorm.transactions.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import uk.co.metadataconsulting.sentinel.modelcatalogue.DataModel

@CompileStatic
class SaveRecordCollectionService {

    CsvImportService csvImportService

    ExcelImportService excelImportService

    RuleFetcherService ruleFetcherService

    RecordCollectionGormService recordCollectionGormService

    UploadFileService uploadFileService

    def executorService

    @CompileDynamic
    @Transactional
    RecordCollectionGormEntity save(RecordFileCommand cmd) {
        final InputStream inputStream = cmd.csvFile.inputStream
        final Integer batchSize = cmd.batchSize
        CsvImport importService = csvImportByContentType(ImportContentType.of(cmd.csvFile.contentType))

        RecordCollectionGormEntity recordCollectionEntity = importService.save(inputStream, batchSize, cmd)
        executorService.submit {
            UploadFileResult uploadFileResult = uploadFileService.uploadFile(recordCollectionEntity.id, cmd.csvFile)
            recordCollectionGormService.updateFileUrl(recordCollectionEntity.id, uploadFileResult.fileUrl)
        }
        List<DataModel> dataModelList = ruleFetcherService.fetchDataModels()?.dataModels
        DataModel dataModel = dataModelList?.find { it.id == cmd.dataModelId }
        if (!dataModel) {
            transactionStatus.setRollbackOnly()
            return null
        }
        recordCollectionGormService.associateWithDataModel(recordCollectionEntity, dataModel)

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
