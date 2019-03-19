package uk.co.metadataconsulting.monitor

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic

@CompileStatic
class RecordPortionGormService {

    @ReadOnly
    List<RecordPortionGormEntity> findAllByRecord(RecordGormEntity record) {
        queryByRecord(record).list()
    }

    DetachedCriteria<RecordPortionGormEntity> queryByRecord(RecordGormEntity recordParam) {
        RecordPortionGormEntity.where { record == recordParam }
    }

    @ReadOnly
    List<RecordPortionGormEntity> findAllByRecordId(Long recordId) {
        queryByRecord(RecordGormEntity.load(recordId)).list()
    }

    @ReadOnly
    Number countByRecordId(Long recordId) {
        queryByRecord(RecordGormEntity.load(recordId)).count()
    }

    @ReadOnly
    Number count() {
        RecordPortionGormEntity.count()
    }

    @Transactional
    void updateWithValidationResult(RecordPortionGormEntity recordPortionGormEntity, ValidationResult validationResult) {
        recordPortionGormEntity.with {
            name = validationResult.name
            status = validationResult.status
            reason = validationResult.reason
            numberOfRulesValidatedAgainst = validationResult.numberOfRulesValidatedAgainst
        }
        recordPortionGormEntity.save()
    }
}