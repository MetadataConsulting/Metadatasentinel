package uk.co.metadataconsulting.sentinel

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic

@CompileStatic
class RecordPortionMappingGormService {

    @ReadOnly
    List<Long> findIdsByRecordCollectionId(Long recordCollectionId) {
        queryByRecordCollectionId(recordCollectionId).projections {
            property('id')
        }.list() as List<Long>
    }

    DetachedCriteria<RecordPortionMappingGormEntity> queryByRecordCollectionId(Long recordCollectionId) {
        RecordPortionMappingGormEntity.where {
            recordCollectionMapping.recordCollection == RecordCollectionGormEntity.load(recordCollectionId)
        }
    }

    @ReadOnly
    List<RecordPortionMapping> findAllByRecordCollectionId(Long recordCollectionId) {
        queryByRecordCollectionId(recordCollectionId).list().collect { RecordPortionMappingGormEntity recordPortionMapping ->
            new RecordPortionMapping(id: recordPortionMapping.id,
                    header: recordPortionMapping.header,
                    gormUrl: recordPortionMapping.gormUrl)
        }
    }

    @Transactional
    void updateGormUrls(List<UpdateGormUrlCommand> updateGormUrlCommands) {
        if ( updateGormUrlCommands ) {
            for ( UpdateGormUrlCommand updateGormUrlCommand : updateGormUrlCommands ) {
                CharSequence query = 'UPDATE RecordPortionMappingGormEntity mapping SET mapping.gormUrl = :gormUrl WHERE mapping.id = :id'
                RecordPortionMappingGormEntity.executeUpdate(query, [
                        gormUrl: updateGormUrlCommand.gormUrl,
                        id: updateGormUrlCommand.id
                ])
            }
        }
    }
}