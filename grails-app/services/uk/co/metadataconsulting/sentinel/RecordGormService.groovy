package uk.co.metadataconsulting.sentinel

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
    RecordGormEntity save(RecordCollectionGormEntity recordCollection, List<RecordPortion> portionList) {
        RecordGormEntity record = new RecordGormEntity()
        record.recordCollection = recordCollection
        for ( RecordPortion portion : portionList ) {
            RecordPortionGormEntity recordPortion = new RecordPortionGormEntity(
                    metatadataDomainEntity: portion.metadataDomainEntity,
                    value: portion.value,
                    valid: portion.valid)
            record.addToPortions(recordPortion)
        }
        if ( !record.save() ) {
            log.warn '{}', errorsMsg(record, messageSource)
        }
        record
    }

    @ReadOnly
    List<RecordGormEntity> findAllByRecordCollectionId(Long recordCollectionId, PaginationQuery paginationQuery) {
        RecordCollectionGormEntity recordCollection = RecordCollectionGormEntity.load(recordCollectionId)
        DetachedCriteria<RecordGormEntity> query = queryByRecordCollection(recordCollection)
        query.list(paginationQuery.toMap())
    }
}