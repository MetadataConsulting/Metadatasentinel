package uk.co.metadataconsulting.sentinel.modelcatalogue

import groovy.transform.CompileStatic

@CompileStatic
class CatalogueElement {
    Long id
    String lastUpdated
    String modelCatalogueId
    String name
    DataModel dataModel
    DomainEnum domain
    StatusEnum status
}
