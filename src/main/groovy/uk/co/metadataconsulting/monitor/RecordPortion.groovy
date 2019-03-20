package uk.co.metadataconsulting.monitor

import groovy.transform.Canonical
import groovy.transform.CompileStatic

@Canonical
@CompileStatic
/**
 * non-GORM version of RecordPortionGormEntity (i.e. a "Cell" in a "Table")
 */
class RecordPortion {
    String header
    String name
    String value
    ValidationStatus status = ValidationStatus.NOT_VALIDATED
    String reason
    Integer numberOfRulesValidatedAgainst = 0

    static List<String> toHeaderList() {
        ['name', 'value', 'numberOfRulesValidatedAgainst','status', 'reason']
    }

    List<String> toList() {
        toHeaderList().collect { String propertyName ->
            this.getProperty(propertyName)?.toString() ?: ''
        }
    }

    String toCsv(String separator = ';') {
        toList().join(separator)
    }
}
