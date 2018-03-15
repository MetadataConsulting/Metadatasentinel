package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.sentinel.modelcatalogue.CatalogueElement
import uk.co.metadataconsulting.sentinel.modelcatalogue.CatalogueElements

@CompileStatic
class RecordCollectionMappingController {

    static allowedMethods = [
            update: 'POST',
            catalogueElements: 'GET'
    ]

    RuleFetcherService ruleFetcherService

    def update(Long recordCollectionId) {

    }

    def catalogueElements(Long dataModelId) {
        CatalogueElements catalogueElements = ruleFetcherService.fetchCatalogueElements(dataModelId)
        List<CatalogueElement> catalogueElementList = []
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
        [catalogueElements: catalogueElementList]
    }
}