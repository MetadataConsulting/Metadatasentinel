package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic

@CompileStatic
class RecordPortionUtils {

    static RecordPortion of(RecordPortionGormEntity recordPortionGormEntity) {
        new RecordPortion(
            header: recordPortionGormEntity.header,
            name: recordPortionGormEntity.name,
            value: recordPortionGormEntity.value,
            status: recordPortionGormEntity.status,
            reason: recordPortionGormEntity.reason,
            numberOfRulesValidatedAgainst: recordPortionGormEntity.numberOfRulesValidatedAgainst)
    }
}
