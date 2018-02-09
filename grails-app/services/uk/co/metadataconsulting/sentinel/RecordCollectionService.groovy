package uk.co.metadataconsulting.sentinel

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileStatic

@CompileStatic
class RecordCollectionService implements GrailsConfigurationAware {

    RecordGormService recordGormService

    RecordService recordService

    int pageSize

    void validate(Long recordCollectionId) {
        int total = recordGormService.countByRecordCollectionId(recordCollectionId) as int
        for (int offset = 0; offset < total; offset = (offset + pageSize)) {
            PaginationQuery paginationQuery = new PaginationQuery(max: pageSize, offset: offset)
            List<RecordGormEntity> recordGormEntityList = recordGormService.findAllByRecordCollectionId(recordCollectionId, paginationQuery)
            for ( RecordGormEntity recordGormEntity : recordGormEntityList ) {
                recordService.validate(recordGormEntity.id)
            }
        }
    }

    @Override
    void setConfiguration(Config co) {
        pageSize = co.getProperty('record.pageSize', Integer, 100)
    }
}