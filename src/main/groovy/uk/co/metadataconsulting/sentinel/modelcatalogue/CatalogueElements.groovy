package uk.co.metadataconsulting.sentinel.modelcatalogue

import groovy.transform.CompileStatic

@CompileStatic
class CatalogueElements {
    List<GormUrlName> dataElements = []
    List<GormUrlName> dataClasses = []
    List<GormUrlName> enumeratedTypes = []
    List<GormUrlName> dataTypes = []
    List<GormUrlName> measurementUnits  = []
    List<GormUrlName> businessRules = []
    List<GormUrlName> tags = []
}
