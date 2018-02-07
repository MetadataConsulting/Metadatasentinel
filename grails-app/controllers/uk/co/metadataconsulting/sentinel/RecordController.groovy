package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import org.springframework.context.MessageSource

@CompileStatic
class RecordController implements ValidateableErrorsMessage {

    MessageSource messageSource

    RecordGormService recordGormService

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
        List<RecordGormEntity> recordList = recordGormService.findAllByRecordCollectionId(cmd.recordCollectionId, paginationQuery)

        [
                recordList: recordList,
                paginationQuery: paginationQuery,
        ]
    }
}