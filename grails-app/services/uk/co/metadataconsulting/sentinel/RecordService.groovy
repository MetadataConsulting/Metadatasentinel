package uk.co.metadataconsulting.sentinel

import grails.gorm.DetachedCriteria
import groovy.transform.CompileStatic

@CompileStatic
class RecordService {
    RecordGormService recordGormService

    List<RecordGormEntity> findAllByRecordCollectionId(Long recordCollectionId, RecordCorrectnessDropdown correctness, PaginationQuery paginationQuery) {
        // TODO Do this with a query
        if ( correctness == RecordCorrectnessDropdown.ALL ) {
            return recordGormService.findAllByRecordCollectionId(recordCollectionId, paginationQuery)
        }
        DetachedCriteria<RecordGormEntity> query = recordGormService.queryByRecordCollectionId(recordCollectionId)
        query.join('portions')

        boolean valid = false
        if ( correctness == RecordCorrectnessDropdown.VALID ) {
            valid = true
        } else if ( correctness == RecordCorrectnessDropdown.INVALID ) {
            valid = false
        }
        List<RecordGormEntity> all = query.list()
        if ( valid ) {
            all = all.findAll { RecordGormEntity gormEntity -> gormEntity.portions.every { RecordPortionGormEntity portionGormEntity -> portionGormEntity.valid == valid }}
        } else {
            all = all.findAll { RecordGormEntity gormEntity -> gormEntity.portions.any { RecordPortionGormEntity portionGormEntity -> portionGormEntity.valid == valid }}
        }

        all.subList(paginationQuery.offset, Math.min(all.size(), paginationQuery.max))
    }

    Number countByRecordCollection(Long recordCollectionId, RecordCorrectnessDropdown correctness) {
        if ( correctness == RecordCorrectnessDropdown.ALL ) {
            return recordGormService.countByRecordCollection(recordCollectionId)
        }
        0 as Number
    }
}