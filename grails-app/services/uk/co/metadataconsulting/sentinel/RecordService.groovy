package uk.co.metadataconsulting.sentinel

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules

@CompileStatic
class RecordService {
    RecordGormService recordGormService
    ValidateRecordPortionService validateRecordPortionService
    RecordPortionGormService recordPortionGormService

    @CompileDynamic
    @ReadOnly
    Set<Long> findAllInvalidRecordIds() {
        DetachedCriteria<RecordPortionGormEntity> invalidQuery =  RecordPortionGormEntity.where {
            def r = record
            status == ValidationStatus.INVALID
        }.projections {
            property('r.id')
        }
        invalidQuery.list()  as Set<Long>
    }

    @CompileDynamic
    @ReadOnly
    Set<Long> findAllValidRecordIds() {
        Set<Long> ids = findAllInvalidRecordIds()
        def c = RecordGormEntity.createCriteria()
         c.list {
             if ( ids ) {
                 not { 'in'('id', ids) }
             }
             projections {
                property('id')
             }
        } as Set<Long>
    }

    @Transactional
    void validate(Long recordId, List<RecordPortionMapping> recordPortionMappingList, Map<String, ValidationRules> validationRulesMap) {
        DetachedCriteria<RecordGormEntity> query = recordGormService.queryById(recordId)
        query.join('portions')
        RecordGormEntity recordGormEntity = query.get()
        for ( RecordPortionGormEntity recordPortionGormEntity : recordGormEntity.portions ) {
            ValidationResult validationResult = validateRecordPortionService.failureReason(recordPortionGormEntity,
                    recordPortionMappingList,
                    validationRulesMap)
            recordPortionGormService.updateWithValidationResult(recordPortionGormEntity, validationResult)
        }
    }

    List<RecordViewModel> findAllByRecordCollectionId(Long recordCollectionId, RecordCorrectnessDropdown correctness, PaginationQuery paginationQuery) {
        // TODO Do this with a query
        DetachedCriteria<RecordGormEntity> query = recordGormService.queryByRecordCollectionId(recordCollectionId)
        //query.join('portions')

        if ( correctness == RecordCorrectnessDropdown.ALL ) {
            Map args = paginationQuery.toMap()

            List<RecordGormEntity> l = query.list(args)

            List<Long> ids = query.id().list(args) as List<Long>
            Set<Long> invalidRecordIds = findAllInvalidRecordIds()

            return ids.collect { Long id ->
                new RecordViewModel(id: id, valid: !invalidRecordIds.contains(id))
            }
        }

        boolean valid = validForCorrectnes(correctness)

        if ( valid ) {
            return findAllValidRecords(recordCollectionId, paginationQuery).collect {
                new RecordViewModel(id:it, valid: true)
            }
        }
        return findAllInvalidRecords(recordCollectionId, paginationQuery).collect {
            new RecordViewModel(id:it, valid: false)
        }
    }

    boolean validForCorrectnes(RecordCorrectnessDropdown correctness) {
        if ( correctness == RecordCorrectnessDropdown.VALID ) {
            return true
        } else if ( correctness == RecordCorrectnessDropdown.INVALID ) {
            return false
        }
        false
    }

    List<Long> findAllValidRecords(Long recordCollectionId, PaginationQuery paginationQuery) {
        Set<Long> validRecordIds = findAllValidRecordIds()
        if ( !validRecordIds ) {
            return [] as List<Long>
        }
        DetachedCriteria<RecordGormEntity> query = queryValidRecords(recordCollectionId, validRecordIds)
        query.id().list(paginationQuery.toMap()) as List<Long>
    }

    List<Long> findAllInvalidRecords(Long recordCollectionId, PaginationQuery paginationQuery) {
        Set<Long> invalidRecordIds = findAllInvalidRecordIds()
        if ( !invalidRecordIds ) {
            return [] as List<Long>
        }
        DetachedCriteria<RecordGormEntity> query = queryInvalidRecords(recordCollectionId, invalidRecordIds)
        query.id().list(paginationQuery.toMap()) as List<Long>
    }

    Number countValidRecords(Long recordCollectionId) {
        Set<Long> validRecordIds = findAllValidRecordIds()
        if ( !validRecordIds ) {
            return 0 as Number
        }
        DetachedCriteria<RecordGormEntity> q = queryValidRecords(recordCollectionId, validRecordIds)
        q.count()
    }

    DetachedCriteria<RecordGormEntity> queryValidRecords(Long recordCollectionId, Set<Long> validRecordIds) {
        DetachedCriteria<RecordGormEntity> query = recordGormService.queryByRecordCollectionId(recordCollectionId)
        query.where {
            (id in validRecordIds)
        }
    }

    DetachedCriteria<RecordGormEntity> queryInvalidRecords(Long recordCollectionId, Set<Long> invalidRecordIds) {
        DetachedCriteria<RecordGormEntity> query = recordGormService.queryByRecordCollectionId(recordCollectionId)
        query.where {
            id in invalidRecordIds
        }
    }

    Number countInvalidRecords(Long recordCollectionId) {
        Set<Long> invalidRecordIds = findAllInvalidRecordIds()
        if ( !invalidRecordIds ) {
            return 0 as Number
        }
        DetachedCriteria<RecordGormEntity> query = queryInvalidRecords(recordCollectionId, invalidRecordIds)
        query.count()
    }

    Number countByRecordCollectionIdAndCorrectness(Long recordCollectionId, RecordCorrectnessDropdown correctness) {
        if ( correctness == RecordCorrectnessDropdown.ALL ) {
            return recordGormService.countByRecordCollectionId(recordCollectionId)
        }
        boolean valid = validForCorrectnes(correctness)
        valid ? countValidRecords(recordCollectionId) : countInvalidRecords(recordCollectionId)
    }
}