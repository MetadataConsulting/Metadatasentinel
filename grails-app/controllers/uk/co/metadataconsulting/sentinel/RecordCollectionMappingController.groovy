package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.sentinel.modelcatalogue.CatalogueElements
import uk.co.metadataconsulting.sentinel.modelcatalogue.GormUrlName

@CompileStatic
class RecordCollectionMappingController {

    static allowedMethods = [
            update: 'POST',
            catalogueElements: 'GET'
    ]

    RuleFetcherService ruleFetcherService
    RecordPortionMappingGormService recordPortionMappingGormService

    def update() {
        Long recordCollectionId = params.long('recordCollectionId')
        List<Long> recordPortionMappingIds = recordPortionMappingGormService.findIdsByRecordCollectionId(recordCollectionId)
        List<UpdateGormUrlCommand> cmds = []
        for ( Long id : recordPortionMappingIds ) {
            String gormUrl = params["catalogueElement${id}"]
            if ( gormUrl ) {
                cmds << new UpdateGormUrlCommand(id: id, gormUrl: gormUrl)
            }
        }
        recordPortionMappingGormService.updateGormUrls(cmds)

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