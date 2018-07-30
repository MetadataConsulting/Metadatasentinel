package uk.co.metadataconsulting.sentinel

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.context.MessageSource

@Slf4j
@CompileStatic
class RecordCollectionGormService implements GormErrorsMessage {

    MessageSource messageSource

    @ReadOnly
    RecordCollectionGormEntity find(Serializable id) {
        RecordCollectionGormEntity.get(id)
    }

    @ReadOnly
    RecordCollectionGormEntity findByDatasetName(String datasetName) {
        queryByDatasetName(datasetName).get()
    }

    @ReadOnly
    List<RecordCollectionGormEntity> findAll(Map args) {
        RecordCollectionGormEntity.where {}.list(args)
    }

    @Transactional
    void saveRecordCollectionMappingWithHeaders(RecordCollectionGormEntity recordCollection, List<String> headers) {
        if ( headers ) {
            for ( String header : headers ) {
                RecordCollectionMappingGormEntity recordPortionMapping = new RecordCollectionMappingGormEntity(header: header)
                recordCollection.addToMappings(recordPortionMapping)
            }
        }
        if ( !recordCollection.save() ) {
            log.warn( 'error saving mappings {}', headers)
        }
    }

    @Transactional
    RecordCollectionGormEntity save(String datasetName) {
        RecordCollectionGormEntity recordCollection = new RecordCollectionGormEntity()
        recordCollection.datasetName = datasetName
        if ( !recordCollection.save(validate:false) ) {
            log.warn '{}', errorsMsg(recordCollection, messageSource)
        }
        recordCollection
    }

    @ReadOnly
    Number count() {
        RecordCollectionGormEntity.count()
    }

    @Transactional
    void delete(Serializable id) {
        RecordCollectionGormEntity.get(id).delete()
    }

    @Transactional
    void deleteByDatasetName(String datasetName) {
        queryByDatasetName(datasetName).get()?.delete()
    }

    DetachedCriteria<RecordCollectionGormEntity> queryByDatasetName(String name) {
        RecordCollectionGormEntity.where {
            datasetName == name
        }
    }

    @ReadOnly
    List<RecordCollectionGormEntity> findAllInIds(Collection<Long> ids) {
        if ( !ids ) {
            return []
        }
        queryAllInIds(ids).list()
    }

    DetachedCriteria<RecordCollectionGormEntity> queryAllInIds(Collection<Long> ids) {
        RecordCollectionGormEntity.where { id in ids }
    }
}