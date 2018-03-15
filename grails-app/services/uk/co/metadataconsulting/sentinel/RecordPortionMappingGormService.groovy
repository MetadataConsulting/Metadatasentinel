package uk.co.metadataconsulting.sentinel

import grails.gorm.transactions.ReadOnly
import groovy.transform.CompileStatic

@CompileStatic
class RecordPortionMappingGormService {

    @ReadOnly
    List<RecordPortionMapping> findAllByRecordCollectionId(Long recordCollectionId) {
        RecordPortionMappingGormEntity.where {
            recordCollectionMapping.recordCollection == RecordCollectionGormEntity.load(recordCollectionId)
        }.list().collect { RecordPortionMappingGormEntity recordPortionMapping ->
            new RecordPortionMapping(id: recordPortionMapping.id,
                    header: recordPortionMapping.header,
                    gormUrl: recordPortionMapping.gormUrl)
        }
    }
}