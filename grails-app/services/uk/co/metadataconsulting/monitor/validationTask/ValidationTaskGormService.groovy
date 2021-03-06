package uk.co.metadataconsulting.monitor.validationTask

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j


@Slf4j
@CompileStatic
class ValidationTaskGormService {
//    /**
//     * Cascading delete
//     * @param validationTaskId
//     */
//    void delete(Long validationTaskId) {
//        ValidationTask validationTask = getValidationTask(validationTaskId)
//        queryByRecordCollection(validationTask).list().each {it.delete(flush:true)}
//        validationTask.delete(flush:true)
//
//    }
//
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

}