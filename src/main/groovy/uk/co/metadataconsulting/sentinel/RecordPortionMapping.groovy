package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import groovy.transform.ToString

@ToString
@CompileStatic
class RecordPortionMapping {
    Long id
    String header
    String gormUrl
    Long dataModelId

    static RecordPortionMapping of(RecordCollectionMappingGormEntity gormEntity) {
        new RecordPortionMapping(id: gormEntity.id,
                header: gormEntity.header,
                gormUrl: gormEntity.gormUrl,
                dataModelId: gormEntity.dataModelId)
    }
}
