package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import org.springframework.context.MessageSource

@CompileStatic
class RecordController implements ValidateableErrorsMessage {

    MessageSource messageSource

    RecordService recordService

    static allowedMethods = [
            index: 'GET',
    ]

    def index(RecordIndexCommand cmd) {
        if ( cmd.hasErrors() ) {
            flash.error = errorsMsg(cmd, messageSource)
            render view: 'index'
            return
        }

        PaginationQuery paginationQuery = cmd.toPaginationQuery()
        List<RecordViewModel> recordList = recordService.findAllByRecordCollectionId(cmd.recordCollectionId, cmd.correctness, paginationQuery)

        Number recordTotal = recordService.countByRecordCollection(cmd.recordCollectionId, cmd.correctness)
        [
                correctness: cmd.correctness,
                recordCollectionId: cmd.recordCollectionId,
                recordList: recordList,
                paginationQuery: paginationQuery,
                recordTotal: recordTotal,
        ]
    }
}