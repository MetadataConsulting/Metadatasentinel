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
}