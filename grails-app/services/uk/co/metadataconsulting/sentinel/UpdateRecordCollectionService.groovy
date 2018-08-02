package uk.co.metadataconsulting.sentinel

import grails.gorm.transactions.Transactional
import grails.validation.ValidationException
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import uk.co.metadataconsulting.sentinel.modelcatalogue.DataModel

@CompileStatic
class UpdateRecordCollectionService {

    RecordCollectionGormService recordCollectionGormService

    RuleFetcherService ruleFetcherService

    @CompileDynamic
    @Transactional
    RecordCollectionGormEntity update(UpdateRecordCollectionCommand cmd) throws ValidationException {

        RecordCollectionGormEntity recordCollectionGormEntity = recordCollectionGormService.update(cmd.recordCollectionId, cmd)
        if (!recordCollectionGormEntity || recordCollectionGormEntity.hasErrors()) {
            transactionStatus.setRollbackOnly()
            return null
        }
        List<DataModel> dataModelList = ruleFetcherService.fetchDataModels()?.dataModels
        DataModel dataModel = dataModelList?.find { it.id == cmd.dataModelId }
        if (!dataModel) {
            transactionStatus.setRollbackOnly()
            return null
        }
        recordCollectionGormService.associateWithDataModel(cmd.recordCollectionId, dataModel)
        recordCollectionGormEntity
    }
}