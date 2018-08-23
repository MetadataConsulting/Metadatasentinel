package uk.co.metadataconsulting.sentinel.validationTask

import org.springframework.context.MessageSource
import uk.co.metadataconsulting.sentinel.PaginationQuery
import uk.co.metadataconsulting.sentinel.RecordCollectionGormEntity
import uk.co.metadataconsulting.sentinel.RecordFileCommand
import uk.co.metadataconsulting.sentinel.RuleFetcherService
import uk.co.metadataconsulting.sentinel.SaveRecordCollectionService
import uk.co.metadataconsulting.sentinel.ValidateableErrorsMessage

class ValidationTaskController implements ValidateableErrorsMessage {

    static allowedMethods = [
            index: 'GET',
//            edit: 'GET',
            importCsv: 'GET',
            uploadCsv: 'POST',
//            validate: 'POST',
//            delete: 'POST',
//            headersMapping: 'GET',
//            cloneMapping: 'GET',
//            cloneSave: 'POST',
//            export: 'GET',
//            update: 'POST'
    ]

    SaveRecordCollectionService saveRecordCollectionService

    RuleFetcherService ruleFetcherService

    MessageSource messageSource

    ValidationTaskService validationTaskService

    ValidationTaskGormService validationTaskGormService

    int defaultPaginationMax = 25
    int defaultPaginationOffset = 0

    def index() {

        Integer max = params.int('max') ?: defaultPaginationMax
        Integer offset = params.int('offset') ?: defaultPaginationOffset
        PaginationQuery paginationQuery = new PaginationQuery(max: max, offset: offset)

        indexModel(paginationQuery)
    }

    protected Map indexModel(PaginationQuery paginationQuery) {
        List<ValidationTask> validationTaskList = validationTaskGormService.findAll(paginationQuery.toMap())
        Number validationTaskListTotal = validationTaskGormService.count()
        [
                validationTaskList: validationTaskList,
                paginationQuery: paginationQuery,
                validationTaskTotal: validationTaskListTotal,
        ]
    }

    /**
     * Copied from RecordCollectionController
     * @return
     */
    def importCsv() {
        List dataModelList = ruleFetcherService.fetchDataModels()?.dataModels
        [dataModelList: dataModelList]
    }

    /**
     * Copied from RecordCollectionController. Called from importCsv view when CSV and accompanying details are submitted to create new Validation Task.
     * @param cmd
     * @return
     */
    def uploadCsv(RecordFileCommand cmd) {
        if ( cmd.hasErrors() ) {
            flash.error = errorsMsg(cmd, messageSource)
            List dataModelList = ruleFetcherService.fetchDataModels()?.dataModels
            render view: 'importCsv', model: [
                    dataModelList: dataModelList,
                    datasetName: cmd.datasetName,
                    dataModelId: cmd.dataModelId,
                    about: cmd.about,
            ]
            return
        }

        log.debug 'Content Type {}', cmd.csvFile.contentType
        RecordCollectionGormEntity recordCollectionGormEntity = saveRecordCollectionService.save(cmd)
        ValidationTask validationTask = validationTaskService.newValidationTaskFromRecordCollection(recordCollectionGormEntity)

        redirect controller: 'recordCollection',
                action: 'headersMapping',
                params: [recordCollectionId: recordCollectionGormEntity.id]
    }
}
