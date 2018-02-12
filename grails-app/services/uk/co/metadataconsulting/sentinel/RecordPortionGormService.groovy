package uk.co.metadataconsulting.sentinel

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
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
}