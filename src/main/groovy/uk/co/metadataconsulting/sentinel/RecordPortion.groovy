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

    static String toHeaderCsv(String separator = ';') {
        List l = ['name', 'value', 'status', 'reason', 'numberOfRulesValidatedAgainst']
        l.join(separator)
    }

    String toCsv(String separator = ';') {
        List l = [name ?: '', value ?: '', status.toString() ?: '', reason ?: '', numberOfRulesValidatedAgainst]
        l.join(separator)
    }
}
