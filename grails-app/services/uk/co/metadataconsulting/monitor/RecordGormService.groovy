package uk.co.metadataconsulting.monitor

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.context.MessageSource

@Slf4j
@CompileStatic
class RecordGormService implements GormErrorsMessage {
    MessageSource messageSource

    @ReadOnly
    List<RecordGormEntity> findAllByRecordCollection(RecordCollectionGormEntity recordCollection, Map args) {
        queryByRecordCollection(recordCollection).list(args)
    }

    @ReadOnly
    Number countByRecordCollection(RecordCollectionGormEntity recordCollection) {
        queryByRecordCollection(recordCollection).count()
    }

    DetachedCriteria<RecordGormEntity> queryByRecordCollection(RecordCollectionGormEntity recordCollectionParam) {
        RecordGormEntity.where { recordCollection == recordCollectionParam }
    }

    DetachedCriteria<RecordGormEntity> queryByRecordCollectionId(Long recordCollectionId) {
        RecordCollectionGormEntity recordCollection = RecordCollectionGormEntity.load(recordCollectionId)
        queryByRecordCollection(recordCollection)

    }

    @Transactional
    RecordGormEntity save(RecordGormEntity recordGormEntity) {
        if ( !recordGormEntity ) {
            return recordGormEntity
        }

        if ( !recordGormEntity.save() ) {
            log.warn '{}', errorsMsg(recordGormEntity, messageSource)
        }
        recordGormEntity
    }

    @Transactional
    /**
     * Save a Record for RecordCollection recordCollection from the list of RecordPortions portionList
     */
    RecordGormEntity save(RecordCollectionGormEntity recordCollection, List<RecordPortion> portionList) {
        RecordGormEntity record = new RecordGormEntity()
        record.recordCollection = recordCollection
        for ( RecordPortion portion : portionList ) {
            if((portion.value.isEmpty())|(portion.value == " ")){
                portion.value = 'N/A'
            }
            RecordPortionGormEntity recordPortion = recordPortionGormEntityOfRecordPortion(portion)

            record.addToPortions(recordPortion)

        }
        if ( !record.save() ) {
            log.warn '{}', errorsMsg(record, messageSource)
        }
        record
    }

    RecordPortionGormEntity recordPortionGormEntityOfRecordPortion(RecordPortion portion) {
        new RecordPortionGormEntity(
                header: portion.header,
                name: portion.name,
                value: portion.value,
                status: portion.status,
                reason: portion.reason,
                numberOfRulesValidatedAgainst: portion.numberOfRulesValidatedAgainst)
    }

    @ReadOnly
    Number countByRecordCollectionId(Long recordCollectionId) {
        RecordCollectionGormEntity recordCollection = RecordCollectionGormEntity.load(recordCollectionId)
        queryByRecordCollection(recordCollection).count()
    }

    @ReadOnly
    List<RecordGormEntity> findAllByRecordCollectionId(Long recordCollectionId, PaginationQuery paginationQuery) {
        DetachedCriteria<RecordGormEntity> query = queryByRecordCollectionId(recordCollectionId)
        query.list(paginationQuery.toMap())
    }

    @ReadOnly
    List<RecordGormEntity> findAllByRecordCollectionId(Long recordCollectionId ) {
        DetachedCriteria<RecordGormEntity> query = queryByRecordCollectionId(recordCollectionId)
        return query as List<RecordGormEntity>

    }

    @ReadOnly
    RecordGormEntity findById(Long recordId, List<String> joinProperties = null) {
        DetachedCriteria<RecordGormEntity> query = queryById(recordId)
        if ( joinProperties ) {
            for ( String propertyName : joinProperties ) {
                query.join(propertyName)
            }
        }
        query.get()
    }

    DetachedCriteria<RecordGormEntity> queryById(Long recordId) {
        RecordGormEntity.where { id == recordId }
    }

    @ReadOnly
    Number count() {
        RecordGormEntity.count()
    }

    @ReadOnly
    List<RecordGormEntity> findAllByIds(List<Long> ids, List<String> propertiesToJoin = ['portions']) {
        DetachedCriteria<RecordGormEntity> query = queryAllByIds(ids)
        if ( propertiesToJoin ) {
            for ( String propertyName : propertiesToJoin ) {
                query.join('portions')
            }
        }
        query.list()
    }

    DetachedCriteria<RecordGormEntity> queryAllByIds(List<Long> ids) {
        RecordGormEntity.where { id in ids }
    }
}