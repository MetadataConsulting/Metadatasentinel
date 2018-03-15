package uk.co.metadataconsulting.sentinel

import grails.gorm.services.Service

@Service(RecordPortionMappingGormEntity)
interface RecordPortionMappingGormDataService {
    RecordPortionMappingGormEntity save(String header)
    RecordPortionMappingGormEntity update(Serializable id, String gormUrl)
}