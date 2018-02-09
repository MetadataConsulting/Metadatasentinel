package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import org.springframework.context.MessageSource

@CompileStatic
class RecordController implements ValidateableErrorsMessage {

    MessageSource messageSource

    RecordService recordService

    RecordPortionGormService recordPortionGormService

    static allowedMethods = [
            index: 'GET',
            validate: 'POST',
            show: 'GET'
    ]

    def index(RecordIndexCommand cmd) {
        if ( cmd.hasErrors() ) {
            flash.error = errorsMsg(cmd, messageSource)
            render view: 'index'
            return
        }

        PaginationQuery paginationQuery = cmd.toPaginationQuery()
        List<RecordViewModel> recordList = recordService.findAllByRecordCollectionId(cmd.recordCollectionId, cmd.correctness, paginationQuery)

        Number recordTotal = recordService.countByRecordCollectionIdAndCorrectness(cmd.recordCollectionId, cmd.correctness)
        [
                correctness: cmd.correctness,
                recordCollectionId: cmd.recordCollectionId,
                recordList: recordList,
                paginationQuery: paginationQuery,
                recordTotal: recordTotal,
        ]
    }

    def validate(Long recordId) {

        recordService.validate(recordId)

        flash.message = messageSource.getMessage('record.validation', [] as Object[],'Record validated again', request.locale)

        redirect action: 'show', controller: 'record', params: [recordId: recordId]
    }

    def show(Long recordId) {

        List<RecordPortionGormEntity> recordPortionList = recordPortionGormService.findAllByRecordId(recordId)
        Number recordPortionTotal = recordPortionGormService.countByRecordId(recordId)
        [
                recordId: recordId,
                recordPortionList: recordPortionList,
                recordPortionTotal: recordPortionTotal
        ]
    }
}