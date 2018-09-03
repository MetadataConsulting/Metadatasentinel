package uk.co.metadataconsulting.monitor

import grails.gorm.services.Service
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@Service(RecordCollectionMappingGormEntity)
interface RecordPortionMappingGormDataService {

    @Transactional
    RecordCollectionMappingGormEntity save(String header, RecordCollectionGormEntity recordCollection)

    @Transactional
    RecordCollectionMappingGormEntity update(Serializable id, String gormUrl)

    @Transactional
    RecordCollectionMappingGormEntity update(Serializable id, String gormUrl, String name)

    @ReadOnly
    Number count()
}