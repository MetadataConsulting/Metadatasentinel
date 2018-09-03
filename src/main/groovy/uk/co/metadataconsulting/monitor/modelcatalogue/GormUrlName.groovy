package uk.co.metadataconsulting.monitor.modelcatalogue

import groovy.transform.CompileStatic
import groovy.transform.ToString
import uk.co.metadataconsulting.monitor.RecordPortionMapping

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
    String combinedGormUrlName

    static String gormUrlNameSeparator = "####"

    String getCombinedGormUrlName() {
        return combineGormUrlName(gormUrl, name)
    }

    static String combineGormUrlName(String gormUrl, String name) {
        return gormUrl + gormUrlNameSeparator + name
    }

    static GormUrlName from(RecordPortionMapping recordPortionMapping, Long dataModelId) {
        return new GormUrlName(gormUrl: recordPortionMapping.gormUrl, name: recordPortionMapping.name, dataModelId: dataModelId)
    }
}


