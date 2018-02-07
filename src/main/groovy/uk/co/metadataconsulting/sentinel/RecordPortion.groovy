package uk.co.metadataconsulting.sentinel

import groovy.transform.Canonical
import groovy.transform.CompileStatic

@Canonical
@CompileStatic
class RecordPortion {
    String metadataDomainEntity
    String value
    Boolean valid
}
