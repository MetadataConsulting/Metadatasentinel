package uk.co.metadataconsulting.monitor

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import uk.co.metadataconsulting.monitor.modelcatalogue.ElasticSearchCatalogueElementDocumentProjection
import uk.co.metadataconsulting.monitor.modelcatalogue.GormUrlName
import uk.co.metadataconsulting.monitor.modelcatalogue.MDXSearchResponse
import uk.co.metadataconsulting.monitor.modelcatalogue.MDXSearchResponseProjection
import uk.co.metadataconsulting.monitor.modelcatalogue.ValidationRules

@Slf4j
@CompileStatic
class RecordCollectionService implements GrailsConfigurationAware {

    RecordGormService recordGormService

    RecordService recordService

    RecordCollectionGormService recordCollectionGormService

    CatalogueElementsService catalogueElementsService

    ReconciliationService reconciliationService

    RuleFetcherService ruleFetcherService

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
    void generateSuggestedMappings(RecordCollectionGormEntity recordCollectionEntity, GenerateMappingSuggestionsBy generateMappingSuggestionsBy = GenerateMappingSuggestionsBy.ElasticSearch) {
        List<String> headersList = recordCollectionEntity.headersList
        Map<String, List<GormUrlName>> suggestionsMap = [:]
        log.info '#generating suggested mapping for recordCollection with dataModel {}', recordCollectionEntity.dataModelId

        Long recordCollectionDataModelId = recordCollectionEntity.dataModelId

        switch (generateMappingSuggestionsBy) {
            case GenerateMappingSuggestionsBy.ReconciliationService:
                if (recordCollectionDataModelId) {
                    List<GormUrlName> calogueElements = catalogueElementsService.findAllByDataModelId(recordCollectionDataModelId)
                    log.info '#headers {} #catalogueElements {}', headersList.size(), calogueElements.size()
                    suggestionsMap = reconciliationService.reconcile(calogueElements, headersList)
                }
                break
            case GenerateMappingSuggestionsBy.ElasticSearch:
                if (recordCollectionDataModelId) {
                    for (String header: headersList) {
                        MDXSearchCommand cmd = new MDXSearchCommand(
                                dataModelId: recordCollectionDataModelId,
                                query: reconciliationService.cleanup(header),
                                fuzzy: true
                        )
                        MDXSearchResponse mdxSearchResponse = ruleFetcherService.mdxSearch(cmd)
                        MDXSearchResponseProjection mdxSearchResponseProjection =  MDXSearchResponseProjection.of(mdxSearchResponse)
                        List<GormUrlName> suggestions = mdxSearchResponseProjection.list.collect {
                            ElasticSearchCatalogueElementDocumentProjection elementDocumentProjection ->
                                GormUrlName.from(elementDocumentProjection, recordCollectionDataModelId)
                        }
                        suggestionsMap.put(header, suggestions)
                    }
                }
                break
            default:
                break
        }

        recordCollectionGormService.saveRecordCollectionMappingWithHeaders(recordCollectionEntity, headersList, suggestionsMap)

    }

    @Override
    void setConfiguration(Config co) {
        pageSize = co.getProperty('record.pageSize', Integer, 100)
    }
}

enum GenerateMappingSuggestionsBy {
    ElasticSearch,
    ReconciliationService
}