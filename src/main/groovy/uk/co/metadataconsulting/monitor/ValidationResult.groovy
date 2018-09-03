package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic

@CompileStatic
class ValidationResult {
    String reason
    String name
    int numberOfRulesValidatedAgainst = 0
    ValidationStatus status = ValidationStatus.NOT_VALIDATED
}
