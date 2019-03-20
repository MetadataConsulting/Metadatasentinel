package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic

@CompileStatic
/**
 * The result of a validation process
 */
class ValidationResult {
    /**
     * Represents the reason validation failed
     */
    String reason
    /**
     * Name of the MDX CatalogueElement being validated
     */
    String name
    int numberOfRulesValidatedAgainst = 0
    ValidationStatus status = ValidationStatus.NOT_VALIDATED
}
