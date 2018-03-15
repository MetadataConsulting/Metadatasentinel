package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.context.MessageSource

@Slf4j
@CompileStatic
class RecordCollectionController implements ValidateableErrorsMessage {

    static allowedMethods = [
            index: 'GET',
            importCsv: 'GET',
            uploadCsv: 'POST',
            validate: 'POST',
            delete: 'POST',
            headersMapping: 'GET'
    ]

    MessageSource messageSource

    CsvImportService csvImportService

    RecordCollectionGormService recordCollectionGormService

    RecordCollectionService recordCollectionService

    ExcelImportService excelImportService

    RecordPortionMappingGormService recordPortionMappingGormService

    RuleFetcherService ruleFetcherService

    def index() {

        Integer max = params.int('max') ?: 25
        Integer offset = params.int('offset') ?: 0
        PaginationQuery paginationQuery = new PaginationQuery(max: max, offset: offset)

        List<RecordCollectionGormEntity> recordCollectionList = recordCollectionGormService.findAll(paginationQuery.toMap())
        Number recordCollectionListTotal = recordCollectionGormService.count()
        [
                recordCollectionList: recordCollectionList,
                paginationQuery: paginationQuery,
                recordCollectionTotal: recordCollectionListTotal,
        ]
    }

    def importCsv() {
        [:]
    }

    def validate(Long recordCollectionId) {
        recordCollectionService.validate(recordCollectionId)

        flash.message = messageSource.getMessage('record.validation', [] as Object[],'Record Collection validation triggered', request.locale)

        redirect action: 'index', controller: 'record', params: [recordCollectionId: recordCollectionId]
    }

    def uploadCsv(RecordFileCommand cmd) {
        if ( cmd.hasErrors() ) {
            flash.error = errorsMsg(cmd, messageSource)
            render view: 'importCsv'
            return
        }

        log.debug 'Content Type {}', cmd.csvFile.contentType
        InputStream inputStream = cmd.csvFile.inputStream
        Integer batchSize = cmd.batchSize
        CsvImport importService = csvImportByContentType (ImportContentType.of(cmd.csvFile.contentType))
        importService.save(inputStream, batchSize)

        redirect controller: 'recordCollection', action: 'index'
    }

    def delete(Long recordCollectionId) {

        if ( !recordCollectionId ) {
            redirect controller: 'recordCollection', action: 'index'
            return
        }

        recordCollectionGormService.delete(recordCollectionId)
        flash.message = messageSource.getMessage('recordCollection.deleted', [] as Object[],'Record Collection deleted', request.locale)

        redirect controller: 'recordCollection', action: 'index'
    }

    def headersMapping(Long recordCollectionId) {
        [
                dataModelList: ruleFetcherService.fetchDataModels()?.dataModels,
                recordCollectionId: recordCollectionId,
                recordPortionMappingList: recordPortionMappingGormService.findAllByRecordCollectionId(recordCollectionId)
        ]
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