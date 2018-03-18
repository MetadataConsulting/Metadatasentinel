package uk.co.metadataconsulting.sentinel

import grails.gorm.services.Service

@Service(RecordCollectionMappingGormEntity)
interface RecordPortionMappingGormDataService {
    RecordCollectionMappingGormEntity save(String header, RecordCollectionGormEntity recordCollection)
    RecordCollectionMappingGormEntity update(Serializable id, String gormUrl, Long dataModelId)
}