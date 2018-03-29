package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
class ValidationResult {
    String reason
    String name
    int numberOfRulesValidatedAgainst = 0
    ValidationStatus status = ValidationStatus.NOT_VALIDATED
}
