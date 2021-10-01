package uk.co.metadataconsulting.monitor.validationTask

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.context.MessageSource
import uk.co.metadataconsulting.monitor.GormErrorsMessage
import uk.co.metadataconsulting.monitor.RecordCollectionGormEntity


@Slf4j
@CompileStatic
class ValidationTaskGormService  implements GormErrorsMessage {

    MessageSource messageSource
    
    @Transactional
    void delete(ValidationTask validationTask) {
        validationTask.delete()
    }

    @ReadOnly
    ValidationTask getValidationTask(Long validationTaskId) {
        return ValidationTask.get(validationTaskId)
    }

    @CompileDynamic
    DetachedCriteria<ValidationPass> queryByRecordCollection(ValidationTask validationTaskParam) {
       return ValidationPass.where { validationTask == validationTaskParam }
    }
//
//    /**
//     * Get Validation Passes for this Validation Task
//     * @param validationTaskId
//     * @return
//     */
//    List<ValidationPass> getValidationPasses(Long validationTaskId) {
//        return queryByRecordCollection(getValidationTask(validationTaskId)).list()
//    }
//

    Long countValidationPasses(Long validationTaskId) {
        return queryByRecordCollection(getValidationTask(validationTaskId)).count() as Long
    }

    @ReadOnly
    List<ValidationTask> findAll(Map args) {
        ValidationTask.where {}.list(args)
    }


    @ReadOnly
    Number count() {
        ValidationTask.count()
    }

    @Transactional
    ValidationTask save(ValidationTask entity) {
        if ( !entity.save(validate:false) ) {
            log.warn '{}', errorsMsg(entity, messageSource)
        }
        entity
    }

    @Transactional
    void update(Long id, RecordCollectionGormEntity recordCollectionGormEntity) {
        ValidationTask validationTask = getValidationTask(id)
        if (validationTask) {
            validationTask.addToValidationPasses(recordCollection: recordCollectionGormEntity)
            save(validationTask)
        }
    }
    @Transactional
    ValidationTask save(String name,
                        RecordCollectionGormEntity recordCollectionGormEntity) {
        ValidationTask validationTask = new ValidationTask(name: name)
        validationTask.addToValidationPasses(recordCollection: recordCollectionGormEntity)
        save(validationTask)
    }
}