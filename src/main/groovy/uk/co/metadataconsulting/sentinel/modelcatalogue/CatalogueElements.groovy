package uk.co.metadataconsulting.sentinel.modelcatalogue

import groovy.transform.CompileStatic

@CompileStatic
class CatalogueElements {
    List<CatalogueElement> dataElements = []
    List<CatalogueElement> dataClasses = []
    List<CatalogueElement> enumeratedTypes = []
    List<CatalogueElement> dataTypes = []
    List<CatalogueElement> measurementUnits  = []
    List<CatalogueElement> businessRules = []
    List<CatalogueElement> tags = []
}
