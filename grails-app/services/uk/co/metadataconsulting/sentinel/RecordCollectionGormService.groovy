package uk.co.metadataconsulting.sentinel

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
    RecordCollectionGormEntity save() {
        RecordCollectionGormEntity recordCollection = new RecordCollectionGormEntity()
        if ( !recordCollection.save() ) {
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
}