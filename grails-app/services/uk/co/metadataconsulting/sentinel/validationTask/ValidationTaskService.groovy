package uk.co.metadataconsulting.sentinel.validationTask

import grails.gorm.transactions.Transactional
import uk.co.metadataconsulting.sentinel.RecordCollectionGormEntity

@Transactional
class ValidationTaskService {

    ValidationTask newValidationTaskFromRecordCollection(RecordCollectionGormEntity recordCollectionGormEntity) {
        ValidationTask validationTask = new ValidationTask(name: recordCollectionGormEntity.datasetName, validationPasses: [])
        validationTask.addToValidationPasses(position: 0, recordCollection: recordCollectionGormEntity)
        validationTask.save(flush:true)
        return validationTask
    }
}
