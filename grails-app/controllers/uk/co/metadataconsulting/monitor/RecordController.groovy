package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic
import org.springframework.context.MessageSource
import uk.co.metadataconsulting.monitor.modelcatalogue.ValidationRules

@CompileStatic
class RecordController implements ValidateableErrorsMessage {

    MessageSource messageSource

    RecordService recordService

    RecordPortionGormService recordPortionGormService
    RecordCollectionMappingEntryGormService recordCollectionMappingEntryGormService
    RecordCollectionGormService recordCollectionGormService
    RuleFetcherService ruleFetcherService

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

        final Long recordCollectionId = cmd.recordCollectionId

        Number allRecordTotal = recordService.countByRecordCollectionIdAndCorrectness(recordCollectionId, RecordCorrectnessDropdown.ALL)
        RecordCollectionGormEntity recordCollectionGormEntity = recordCollectionGormService.find(recordCollectionId)
        if (allRecordTotal == 0 && !recordCollectionGormEntity) {
            redirect(controller: 'recordCollection', action: 'index')
            return
        }

        List<RecordViewModel> recordList = recordService.findAllByRecordCollectionId(recordCollectionId, cmd.correctness, paginationQuery)
        Number invalidRecordTotal = recordService.countByRecordCollectionIdAndCorrectness(recordCollectionId, RecordCorrectnessDropdown.INVALID)
        Number recordTotal = recordService.countByRecordCollectionIdAndCorrectness(recordCollectionId, cmd.correctness)
        Map model = [
                correctness: cmd.correctness,
                recordCollectionId: cmd.recordCollectionId,
                recordList: recordList,
                paginationQuery: paginationQuery,
                recordTotal: recordTotal,
                allRecordTotal: allRecordTotal,
                invalidRecordTotal: invalidRecordTotal,
        ]
        if (recordCollectionGormEntity) {
            model['recordCollectionEntity'] = recordCollectionGormEntity
        }
        model
    }

    def validate(Long recordId, Long recordCollectionId) {
        List<RecordPortionMapping> recordPortionMappingList = recordCollectionMappingEntryGormService.findAllByRecordCollectionId(recordCollectionId)
        Map<String, ValidationRules> validationRulesMap = ruleFetcherService.fetchValidationRulesByMapping(recordPortionMappingList)
        if ( !validationRulesMap ) {
            flash.error = messageSource.getMessage('record.validation.noRules', [] as Object[],'Could not trigger validation. No rules for mapping', request.locale)
            redirect action: 'show', controller: 'record', params: [recordId: recordId, recordCollectionId: recordCollectionId]
            return
        }
        recordService.validate(recordId, recordPortionMappingList, validationRulesMap)
        flash.message = messageSource.getMessage('record.validation', [] as Object[],'Record validated again', request.locale)
        redirect action: 'show', controller: 'record', params: [recordId: recordId, recordCollectionId: recordCollectionId]
    }

    def show(Long recordId, Long recordCollectionId) {

        List<RecordPortionGormEntity> recordPortionList = recordPortionGormService.findAllByRecordId(recordId)
        Number recordPortionTotal = recordPortionGormService.countByRecordId(recordId)
        RecordCollectionGormEntity recordCollectionGormEntity = recordCollectionGormService.find(recordCollectionId)
        [
                recordCollectionEntity: recordCollectionGormEntity,
                recordId: recordId,
                recordPortionList: recordPortionList,
                recordPortionTotal: recordPortionTotal,
                recordCollectionId: recordCollectionId,
                recordPortionMappingList: recordCollectionMappingEntryGormService.findAllByRecordCollectionId(recordCollectionId),
        ]
    }
}