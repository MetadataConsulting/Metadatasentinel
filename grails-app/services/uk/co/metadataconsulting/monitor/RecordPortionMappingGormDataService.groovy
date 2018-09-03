package uk.co.metadataconsulting.monitor

import grails.gorm.services.Service
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@Service(RecordCollectionMappingEntryGormEntity)
interface RecordPortionMappingGormDataService {

    @Transactional
    RecordCollectionMappingEntryGormEntity save(String header, RecordCollectionGormEntity recordCollection)

    @Transactional
    RecordCollectionMappingEntryGormEntity update(Serializable id, String gormUrl)

    @Transactional
    RecordCollectionMappingEntryGormEntity update(Serializable id, String gormUrl, String name)

    @ReadOnly
    Number count()
}