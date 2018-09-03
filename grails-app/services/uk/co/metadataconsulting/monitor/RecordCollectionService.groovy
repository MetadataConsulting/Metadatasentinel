package uk.co.metadataconsulting.monitor

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileStatic
import uk.co.metadataconsulting.monitor.modelcatalogue.ValidationRules

@CompileStatic
class RecordCollectionService implements GrailsConfigurationAware {

    RecordGormService recordGormService

    RecordService recordService

    int pageSize

    void validate(Long recordCollectionId, List<RecordPortionMapping> recordPortionMappingList, Map<String, ValidationRules> validationRulesMap) {
        int total = recordGormService.countByRecordCollectionId(recordCollectionId) as int
        for (int offset = 0; offset < total; offset = (offset + pageSize)) {
            PaginationQuery paginationQuery = new PaginationQuery(max: pageSize, offset: offset)
            List<RecordGormEntity> recordGormEntityList = recordGormService.findAllByRecordCollectionId(recordCollectionId, paginationQuery)
            for ( RecordGormEntity recordGormEntity : recordGormEntityList ) {
                recordService.validate(recordGormEntity.id, recordPortionMappingList, validationRulesMap)
            }
        }
    }

    @Override
    void setConfiguration(Config co) {
        pageSize = co.getProperty('record.pageSize', Integer, 100)
    }
}