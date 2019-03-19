package uk.co.metadataconsulting.monitor

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@CompileStatic
class RecordCollectionMappingEntryGormService {

    RecordPortionMappingGormDataService recordPortionMappingGormDataService

    RecordCollectionGormService recordCollectionGormService

    @ReadOnly
    List<Long> findIdsByRecordCollectionId(Long recordCollectionId) {
        queryByRecordCollectionId(recordCollectionId).projections {
            property('id')
        }.list() as List<Long>
    }

    DetachedCriteria<RecordCollectionMappingEntryGormEntity> queryByRecordCollectionId(Long recordCollectionId) {
        RecordCollectionMappingEntryGormEntity.where {
            recordCollection == RecordCollectionGormEntity.load(recordCollectionId)
        }
    }

    @ReadOnly
    List<RecordPortionMapping> findAllByRecordCollectionId(Long recordCollectionId) {
        queryByRecordCollectionId(recordCollectionId).list().collect { RecordCollectionMappingEntryGormEntity gormEntity ->
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
        List<RecordCollectionMappingEntryGormEntity> fromEntities = queryByRecordCollectionId(fromRecordCollectionId).list()
        List<RecordCollectionMappingEntryGormEntity> toEntities = queryByRecordCollectionId(toRecordCollectionId).list()

        for ( RecordCollectionMappingEntryGormEntity toEntity : toEntities ) {
            RecordCollectionMappingEntryGormEntity fromEntity = fromEntities.find { it.header.equalsIgnoreCase(toEntity.header) }
            if ( fromEntity ) {
                toEntity.with {
                    gormUrl = fromEntity.gormUrl
                }
                toEntity.save()
            }
        }
    }

    @Transactional
    void deleteMappingEntriesFor(Long recordCollectionGormEntityId) {
        RecordCollectionGormEntity recordCollectionGormEntity = recordCollectionGormService.find(recordCollectionGormEntityId)
        queryByRecordCollectionId(recordCollectionGormEntityId).deleteAll()
    }

    @ReadOnly
    @CompileDynamic
    Set<Long> findAllRecordCollectionIdByGormUrlNotNull() {
        // TODO implement this efficiently
        RecordCollectionMappingEntryGormEntity.findAllByGormUrlIsNotNull()?.collect { it.recordCollection.id } as Set<Long>
    }

    @Transactional
    RecordCollectionMappingEntryGormEntity updateGormUrl(Long recordPortionId, String gormUrl) {
        recordPortionMappingGormDataService.update(recordPortionId, gormUrl)
    }

    @Transactional
    RecordCollectionMappingEntryGormEntity updateGormUrlName(Long recordPortionId, String gormUrl, String name) {
        recordPortionMappingGormDataService.update(recordPortionId, gormUrl, name)
    }
}