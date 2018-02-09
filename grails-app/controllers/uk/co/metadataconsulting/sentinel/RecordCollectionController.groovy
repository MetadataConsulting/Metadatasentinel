package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import org.springframework.context.MessageSource

@CompileStatic
class RecordCollectionController implements ValidateableErrorsMessage {

    static allowedMethods = [
            index: 'GET',
            importCsv: 'GET',
            uploadCsv: 'POST',
            validate: 'POST',
    ]

    MessageSource messageSource

    CsvImportService csvImportService

    RecordCollectionGormService recordCollectionGormService

    RecordCollectionService recordCollectionService

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

    def uploadCsv(RecordCsvCommand cmd) {
        if ( cmd.hasErrors() ) {
            flash.error = errorsMsg(cmd, messageSource)
            render view: 'importCsv'
            return
        }

        InputStream inputStream = cmd.csvFile.inputStream
        Integer batchSize = cmd.batchSize
        csvImportService.save(cmd.mapping.split(',') as List<String>, inputStream, batchSize)

        redirect controller: 'recordCollection', action: 'index'
    }
}