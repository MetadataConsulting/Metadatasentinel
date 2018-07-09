package uk.co.metadataconsulting.sentinel

import groovy.transform.Canonical
import groovy.transform.CompileStatic

@Canonical
@CompileStatic
class RecordPortion {
    String header
    String name
    String value
    ValidationStatus status = ValidationStatus.NOT_VALIDATED
    String reason
    Integer numberOfRulesValidatedAgainst = 0

    static List<String> toHeaderList() {
        ['name', 'value', 'status', 'reason', 'numberOfRulesValidatedAgainst']
    }

    List<String> toList() {
        [name ?: '', value ?: '', status.toString() ?: '', reason ?: '', "${numberOfRulesValidatedAgainst}".toString()]
    }

    String toCsv(String separator = ';') {
        toList().join(separator)
    }
}
