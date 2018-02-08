package uk.co.metadataconsulting.sentinel

import groovy.transform.Canonical
import groovy.transform.CompileStatic

@Canonical
@CompileStatic
class RecordPortion {
    String name
    String gormUrl
    String value
    Boolean valid
    String reason
}
