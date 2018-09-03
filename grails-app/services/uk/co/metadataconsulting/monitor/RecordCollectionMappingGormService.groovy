package uk.co.metadataconsulting.monitor

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import groovy.transform.CompileDynamic
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
                recordPortionMappingGormDataService.update(req.id, req.gormUrl)
            }
        }
    }

    @Transactional
    void cloneMapping(Long fromRecordCollectionId, Long toRecordCollectionId) {
        List<RecordCollectionMappingGormEntity> fromEntities = queryByRecordCollectionId(fromRecordCollectionId).list()
        List<RecordCollectionMappingGormEntity> toEntities = queryByRecordCollectionId(toRecordCollectionId).list()

        for ( RecordCollectionMappingGormEntity toEntity : toEntities ) {
            RecordCollectionMappingGormEntity fromEntity = fromEntities.find { it.header.equalsIgnoreCase(toEntity.header) }
            if ( fromEntity ) {
                toEntity.with {
                    gormUrl = fromEntity.gormUrl
                }
                toEntity.save()
            }
        }
    }

    @ReadOnly
    @CompileDynamic
    Set<Long> findAllRecordCollectionIdByGormUrlNotNull() {
        // TODO implement this efficiently
        RecordCollectionMappingGormEntity.findAllByGormUrlIsNotNull()?.collect { it.recordCollection.id } as Set<Long>
    }

    @Transactional
    RecordCollectionMappingGormEntity updateGormUrl(Long recordPortionId, String gormUrl) {
        recordPortionMappingGormDataService.update(recordPortionId, gormUrl)
    }

    @Transactional
    RecordCollectionMappingGormEntity updateGormUrlName(Long recordPortionId, String gormUrl, String name) {
        recordPortionMappingGormDataService.update(recordPortionId, gormUrl, name)
    }
}