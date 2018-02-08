package uk.co.metadataconsulting.sentinel

import grails.gorm.DetachedCriteria
import groovy.transform.CompileStatic

@CompileStatic
class RecordService {
    RecordGormService recordGormService

    List<RecordViewModel> findAllByRecordCollectionId(Long recordCollectionId, RecordCorrectnessDropdown correctness, PaginationQuery paginationQuery) {
        // TODO Do this with a query
        DetachedCriteria<RecordGormEntity> query = recordGormService.queryByRecordCollectionId(recordCollectionId)
        query.join('portions')

        if ( correctness == RecordCorrectnessDropdown.ALL ) {
            return query.list(paginationQuery.toMap()).collect { RecordGormEntity recordGormEntity ->
                boolean valid = !recordGormEntity.portions.any { RecordPortionGormEntity recordPortionGormEntity -> !recordPortionGormEntity.valid }
                new RecordViewModel(id: recordGormEntity.id, valid: valid)
            }
        }

        boolean valid = validForCorrectnes(correctness)
        List<RecordGormEntity> all = valid ? findAllValidRecords(recordCollectionId) : findAllInvalidRecords(recordCollectionId)

        all.subList(paginationQuery.offset, Math.min(all.size(), paginationQuery.max)).collect { RecordGormEntity recordGormEntity ->
            new RecordViewModel(id: recordGormEntity.id, valid: valid)
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

    List<RecordGormEntity> findAllValidRecords(Long recordCollectionId) {
        DetachedCriteria<RecordGormEntity> query = recordGormService.queryByRecordCollectionId(recordCollectionId)
        query.join('portions')
        query.list().findAll { RecordGormEntity gormEntity ->
            gormEntity.portions.every { RecordPortionGormEntity portionGormEntity -> portionGormEntity.valid }
        }
    }

    List<RecordGormEntity> findAllInvalidRecords(Long recordCollectionId) {
        DetachedCriteria<RecordGormEntity> query = recordGormService.queryByRecordCollectionId(recordCollectionId)
        query.join('portions')
        query.list().findAll { RecordGormEntity gormEntity ->
            gormEntity.portions.any { RecordPortionGormEntity portionGormEntity -> !portionGormEntity.valid }
        }
    }

    Number countByRecordCollection(Long recordCollectionId, RecordCorrectnessDropdown correctness) {
        if ( correctness == RecordCorrectnessDropdown.ALL ) {
            return recordGormService.countByRecordCollection(recordCollectionId)
        }
        boolean valid = validForCorrectnes(correctness)
        (valid ? findAllValidRecords(recordCollectionId).size() : findAllInvalidRecords(recordCollectionId).size()) as Number
    }
}