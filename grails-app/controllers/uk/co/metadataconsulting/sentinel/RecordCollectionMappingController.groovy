package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import org.springframework.context.MessageSource
import uk.co.metadataconsulting.sentinel.modelcatalogue.CatalogueElements
import uk.co.metadataconsulting.sentinel.modelcatalogue.GormUrlName

@CompileStatic
class RecordCollectionMappingController {

    static allowedMethods = [
            update: 'POST',
            catalogueElements: 'GET'
    ]

    MessageSource messageSource
    RuleFetcherService ruleFetcherService
    RecordCollectionMappingGormService recordCollectionMappingGormService

    def update() {
        Long recordCollectionId = params.long('recordCollectionId')
        List<Long> recordPortionMappingIds = recordCollectionMappingGormService.findIdsByRecordCollectionId(recordCollectionId)
        if ( recordPortionMappingIds ) {
            List<UpdateGormUrlRequest> cmds = []
            for ( Long id : recordPortionMappingIds ) {
                String gormUrl = params.getProperty("catalogueElement${id}")
                Long dataModelId = params.long("dataModel${id}")
                if ( gormUrl && gormUrl != 'null' && dataModelId ) {
                    cmds << new UpdateGormUrlRequest(id: id, gormUrl: gormUrl, dataModelId: dataModelId)
                }
            }
            recordCollectionMappingGormService.updateGormUrls(cmds)
            flash.message = messageSource.getMessage('recordCollection.mapping.updated', [] as Object[],'Record Collection Mapping updated', request.locale)
        }
        redirect uri: "/recordCollection/$recordCollectionId/mapping".toString()
    }

    def catalogueElements(Long dataModelId) {
        CatalogueElements catalogueElements = ruleFetcherService.fetchCatalogueElements(dataModelId)
        List<GormUrlName> catalogueElementList = []
        if ( catalogueElements ) {
            if ( catalogueElements.dataElements ) {
                catalogueElementList.addAll(catalogueElements.dataElements)
            }
            if ( catalogueElements.dataClasses ) {
                catalogueElementList.addAll(catalogueElements.dataClasses)
            }
            if ( catalogueElements.enumeratedTypes ) {
                catalogueElementList.addAll(catalogueElements.enumeratedTypes)
            }
            if ( catalogueElements.dataTypes ) {
                catalogueElementList.addAll(catalogueElements.dataTypes)
            }
            if ( catalogueElements.measurementUnits  ) {
                catalogueElementList.addAll(catalogueElements.measurementUnits)
            }
            if ( catalogueElements.businessRules ) {
                catalogueElementList.addAll(catalogueElements.businessRules)
            }
            if ( catalogueElements.tags ) {
                catalogueElementList.addAll(catalogueElements.tags)
            }
        }

        [catalogueElements: catalogueElementList]
    }
}