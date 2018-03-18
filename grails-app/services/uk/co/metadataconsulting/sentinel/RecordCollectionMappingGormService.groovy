package uk.co.metadataconsulting.sentinel

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic

@CompileStatic
class RecordCollectionMappingGormService {

    RecordPortionMappingGormDataService recordPortionMappingGormDataService

    @ReadOnly
    List<Long> findIdsByRecordCollectionId(Long recordCollectionId) {
        queryByRecordCollectionId(recordCollectionId).projections {
            property('id')
        }.list() as List<Long>
    }

    DetachedCriteria<RecordCollectionMappingGormEntity> queryByRecordCollectionId(Long recordCollectionId) {
        RecordCollectionMappingGormEntity.where {
            recordCollection == RecordCollectionGormEntity.load(recordCollectionId)
        }
    }

    @ReadOnly
    List<RecordPortionMapping> findAllByRecordCollectionId(Long recordCollectionId) {
        queryByRecordCollectionId(recordCollectionId).list().collect { RecordCollectionMappingGormEntity gormEntity ->
            RecordPortionMapping.of(gormEntity)
        }
    }

    @Transactional
    void updateGormUrls(List<UpdateGormUrlRequest> updateGormUrlCommands) {
        if ( updateGormUrlCommands ) {
            for ( UpdateGormUrlRequest req : updateGormUrlCommands ) {
                recordPortionMappingGormDataService.update(req.id, req.gormUrl, req.dataModelId)
            }
        }
    }
}