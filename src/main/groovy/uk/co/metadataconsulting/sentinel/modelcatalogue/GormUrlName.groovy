package uk.co.metadataconsulting.sentinel.modelcatalogue

import groovy.transform.CompileStatic
import groovy.transform.ToString

@ToString(includes = ['name', 'gormUrl'], includePackage = false, includeNames = false)
@CompileStatic
/**
 * Represents a ModelCatalogue CatalogueElement.
 * Very minimal at the moment: gormUrl identifies it, name is displayed, and dataModelId helps categorize them.
 */
class GormUrlName {
    String gormUrl
    String name
    Long dataModelId
}
