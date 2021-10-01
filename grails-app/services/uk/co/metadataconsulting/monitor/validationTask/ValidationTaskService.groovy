package uk.co.metadataconsulting.monitor.validationTask

import grails.gorm.transactions.Transactional
import uk.co.metadataconsulting.monitor.RecordCollectionGormEntity

@Transactional
class ValidationTaskService {

    ValidationTaskGormService validationTaskGormService

    ValidationTaskProjection validationTaskProjection(Long validationTaskId) {
        ValidationTask validationTask = validationTaskGormService.getValidationTask(validationTaskId)
        return new ValidationTaskProjection(
                id: validationTask.id,
                name: validationTask.name
        )
    }

    @Transactional
    ValidationTask addRecordCollectionToValidationTask(RecordCollectionGormEntity recordCollectionGormEntity, Long validationTaskId) {
        ValidationTask validationTask = validationTaskGormService.getValidationTask(validationTaskId)
        if (validationTask != null) {
            validationTask.addToValidationPasses(recordCollection: recordCollectionGormEntity)
            validationTaskGormService.save(validationTask)
        }
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
