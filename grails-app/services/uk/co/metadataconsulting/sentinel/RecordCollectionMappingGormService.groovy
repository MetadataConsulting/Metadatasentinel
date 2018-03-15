package uk.co.metadataconsulting.sentinel

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic

@CompileStatic
class RecordCollectionMappingGormService {

    RecordPortionMappingGormDataService recordPortionMappingGormDataService

    @Transactional
    void saveRecordCollectionMappingWithHeaders(RecordCollectionGormEntity recordCollection, List<String> headers) {
        RecordCollectionMappingGormEntity recordCollectionMapping = new RecordCollectionMappingGormEntity()
        if ( headers ) {
            for ( String header : headers ) {
                RecordPortionMappingGormEntity recordPortionMapping = new RecordPortionMappingGormEntity(header: header)
                recordCollectionMapping.addToRecordPortionMappings(recordPortionMapping)
            }
        }
        recordCollection.recordCollectionMapping = recordCollectionMapping
        recordCollection.save()
    }
}