package uk.co.metadataconsulting.monitor.modelcatalogue

import groovy.transform.CompileStatic

@CompileStatic
/**
 * Collections of GormUrlNames (representations of CatalogueElements), one for each Domain class of the MDX (roughly).
 */
class CatalogueElements {
    List<GormUrlName> dataElements = []
    List<GormUrlName> dataClasses = []
    List<GormUrlName> enumeratedTypes = []
    List<GormUrlName> dataTypes = []
    List<GormUrlName> measurementUnits  = []
    List<GormUrlName> businessRules = []
    List<GormUrlName> tags = []
}
