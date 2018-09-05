package uk.co.metadataconsulting.monitor

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import uk.co.metadataconsulting.monitor.modelcatalogue.GormUrlName
import uk.co.metadataconsulting.monitor.modelcatalogue.ValidationRules

@Slf4j
@CompileStatic
class RecordCollectionService implements GrailsConfigurationAware {

    RecordGormService recordGormService

    RecordService recordService

    RecordCollectionGormService recordCollectionGormService

    CatalogueElementsService catalogueElementsService

    ReconciliationService reconciliationService

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

    /**
     * Generate suggested mappings from reconciliationService for a recordCollectionEntity.
     * @param recordCollectionEntity
     */
    void generateSuggestedMappings(RecordCollectionGormEntity recordCollectionEntity) {
        List<String> headersList = recordCollectionEntity.headersList
        Map<String, List<GormUrlName>> suggestions = [:]
        log.info '#generating suggested mapping for recordCollection with dataModel {}', recordCollectionEntity.dataModelId

        if (recordCollectionEntity.dataModelId) {
            List<GormUrlName> calogueElements = catalogueElementsService.findAllByDataModelId(recordCollectionEntity.dataModelId)
            log.info '#headers {} #catalogueElements {}', headersList.size(), calogueElements.size()
            suggestions = reconciliationService.reconcile(calogueElements, headersList)
        }
        recordCollectionGormService.saveRecordCollectionMappingWithHeaders(recordCollectionEntity, headersList, suggestions)

    }

    @Override
    void setConfiguration(Config co) {
        pageSize = co.getProperty('record.pageSize', Integer, 100)
    }
}