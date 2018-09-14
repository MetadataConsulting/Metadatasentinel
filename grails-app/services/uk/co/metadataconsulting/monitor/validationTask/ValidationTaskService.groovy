package uk.co.metadataconsulting.monitor.validationTask

import grails.gorm.transactions.Transactional
import org.springframework.transaction.annotation.Isolation
import uk.co.metadataconsulting.monitor.RecordCollectionGormEntity

@Transactional(isolation= Isolation.REPEATABLE_READ)
class ValidationTaskService {

    ValidationTaskGormService validationTaskGormService

    ValidationTaskProjection validationTaskProjection(Long validationTaskId) {
        ValidationTask validationTask = validationTaskGormService.getValidationTask(validationTaskId)
        return new ValidationTaskProjection(
                id: validationTask.id,
                name: validationTask.name
        )
    }

    ValidationTask newValidationTaskFromRecordCollection(RecordCollectionGormEntity recordCollectionGormEntity) {
        ValidationTask validationTask = new ValidationTask(name: "${recordCollectionGormEntity.datasetName} Validation Task", validationPasses: [])
        validationTask.addToValidationPasses(recordCollection: recordCollectionGormEntity)
        validationTask.save(flush:true)
        return validationTask
    }

    ValidationTask addRecordCollectionToValidationTask(RecordCollectionGormEntity recordCollectionGormEntity, Long validationTaskId) {
        ValidationTask validationTask = validationTaskGormService.getValidationTask(validationTaskId)
        validationTask.addToValidationPasses(recordCollection: recordCollectionGormEntity)
        validationTask.save(flush: true)
        return validationTask
    }
}

/**
 * Projection of ValidationTask
 */
class ValidationTaskProjection {
    Long id
    String name
}
