package uk.co.metadataconsulting.monitor.validationTask

import org.springframework.context.MessageSource
import uk.co.metadataconsulting.monitor.PaginationQuery
import uk.co.metadataconsulting.monitor.RecordCollectionGormEntity
import uk.co.metadataconsulting.monitor.RecordFileCommand
import uk.co.metadataconsulting.monitor.RuleFetcherService
import uk.co.metadataconsulting.monitor.SaveRecordCollectionService
import uk.co.metadataconsulting.monitor.ValidateableErrorsMessage

class ValidationTaskController implements ValidateableErrorsMessage {

    static allowedMethods = [
            index: 'GET',
//            edit: 'GET',
            importCsv: 'GET',
            uploadCsv: 'POST',
            show: 'GET',
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

    ValidationPassGormService validationPassGormService

    int defaultPaginationMax = 25
    int defaultPaginationOffset = 0

    def show(Long validationTaskId) {

        Integer max = params.int('max') ?: defaultPaginationMax
        Integer offset = params.int('offset') ?: defaultPaginationOffset
        PaginationQuery paginationQuery = new PaginationQuery(max: max, offset: offset)

        showModel(paginationQuery, validationTaskId)
    }

    def index() {

        Integer max = params.int('max') ?: defaultPaginationMax
        Integer offset = params.int('offset') ?: defaultPaginationOffset
        PaginationQuery paginationQuery = new PaginationQuery(max: max, offset: offset)

        indexModel(paginationQuery)
    }

    protected Map showModel(PaginationQuery paginationQuery, Long validationTaskId) {
        ValidationTask validationTask = validationTaskGormService.getValidationTask(validationTaskId)
        List<ValidationPass> validationPassList = validationPassGormService.findAllValidationPasses(paginationQuery.toMap(), validationTask)

        Number validationPassTotal = validationPassGormService.count()
        [
                validationPassList: validationPassList,
                paginationQuery: paginationQuery,
                validationPassTotal: validationPassTotal,
                validationTask: validationTask,
        ]
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
        Map model = [dataModelList: dataModelList,
                     validationTask: null]
        if (params.validationTaskId) {
            model.validationTask = validationTaskService.validationTaskProjection(params.long('validationTaskId'))
        }
        model
    }

    /**
     * Copied from RecordCollectionController. Called from importCsv view when CSV and accompanying details are submitted to create new Validation Task.
     * Creates a new RecordCollection and makes it part of a ValidationTask.
     * @param cmd
     * @return
     */
    def uploadCsv(ValidationTaskFileCommand cmd) {
        if ( cmd.hasErrors() ) {
            flash.error = errorsMsg(cmd, messageSource)
            List dataModelList = ruleFetcherService.fetchDataModels()?.dataModels
            render view: 'importCsv', model: [
                    dataModelList: dataModelList,
                    datasetName: cmd.datasetName,
                    dataModelId: cmd.dataModelId,
                    validationTask: cmd.validationTaskId == null ? null : validationTaskService.validationTaskProjection(cmd.validationTaskId),
                    about: cmd.about,
            ]
            return
        }

        log.debug 'Content Type {}', cmd.csvFile.contentType
        RecordCollectionGormEntity recordCollectionGormEntity = saveRecordCollectionService.save(RecordFileCommand.of(cmd))
        if (cmd.validationTaskId) {
            ValidationTask validationTask = validationTaskService.addRecordCollectionToValidationTask(recordCollectionGormEntity, cmd.validationTaskId)
        }
        else {
            ValidationTask validationTask = validationTaskService.newValidationTaskFromRecordCollection(recordCollectionGormEntity)
        }


        redirect controller: 'recordCollection',
                action: 'headersMapping',
                params: [recordCollectionId: recordCollectionGormEntity.id]
    }
}
