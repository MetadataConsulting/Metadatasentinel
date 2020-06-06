package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic
import groovy.transform.ToString
import uk.co.metadataconsulting.monitor.modelcatalogue.GormUrlName

@ToString
@CompileStatic
/**
 * Non-GORM representation (i.e. class not in the Grails domain) of RecordCollectionMappingEntryGormEntity.
 * Represents a mapping from a header to a MDX CatalogueElement (identified by gormUrl).
 * Actually a bit poorly named since it's more to do with a "column (header)" than individual RecordPortions (cells).
 */
class RecordPortionMapping {
    Long id
    /**
     * Header for the RecordCollection/DataSet
     */
    String header
    /**
     * Identifies the associated MDX CatalogueElement
     */
    String gormUrl
    /**
     * Name of the associated MDX CatalogueElement
     */
    String name
    /**
     * Combines gormURL and name. This is sort of a hack for the front-end Javascript selection/search library to work properly
     */
    String combinedGormUrlName

    static RecordPortionMapping of(RecordCollectionMappingEntryGormEntity gormEntity) {
        new RecordPortionMapping(id: gormEntity.id,
                header: gormEntity.header,
                gormUrl: gormEntity.gormUrl,
                name: gormEntity.name)
    }

    String getCombinedGormUrlName() {
        return GormUrlName.combineGormUrlName(gormUrl, name)
    }
}
