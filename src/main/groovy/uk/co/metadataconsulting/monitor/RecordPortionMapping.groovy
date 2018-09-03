package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic
import groovy.transform.ToString
import uk.co.metadataconsulting.monitor.modelcatalogue.GormUrlName

@ToString
@CompileStatic
/**
 * Non-GORM representation of RecordCollectionMappingGormEntity.
 */
class RecordPortionMapping {
    Long id
    String header
    String gormUrl
    String name
    String combinedGormUrlName

    static RecordPortionMapping of(RecordCollectionMappingGormEntity gormEntity) {
        new RecordPortionMapping(id: gormEntity.id,
                header: gormEntity.header,
                gormUrl: gormEntity.gormUrl,
                name: gormEntity.name)
    }

    String getCombinedGormUrlName() {
        return GormUrlName.combineGormUrlName(gormUrl, name)
    }
}
