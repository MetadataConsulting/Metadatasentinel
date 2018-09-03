package uk.co.metadataconsulting.monitor.validationTask

import grails.gorm.transactions.Transactional
import uk.co.metadataconsulting.monitor.RecordCollectionGormEntity

@Transactional
class ValidationTaskService {

    ValidationTask newValidationTaskFromRecordCollection(RecordCollectionGormEntity recordCollectionGormEntity) {
        ValidationTask validationTask = new ValidationTask(name: "${recordCollectionGormEntity.datasetName} Validation Task", validationPasses: [])
        validationTask.addToValidationPasses(position: 0, recordCollection: recordCollectionGormEntity)
        validationTask.save(flush:true)
        return validationTask
    }
}
