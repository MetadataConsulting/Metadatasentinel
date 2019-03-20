package uk.co.metadataconsulting.monitor.modelcatalogue

import groovy.transform.Canonical
import groovy.transform.CompileStatic

@Canonical
@CompileStatic
/**
 * Representation of a Drools Rule, which are Validation Rules/Business Rules in the MDX
 */
class ValidationRule {
    /**
     * Name of the ValidationRule
     */
    String name
    /**
     * Mapping from the identifiers (global variables used in Drools Rules) to GORM URLs (which identify CatalogueElements in the MDX)
     */
    Map<String, String> identifiersToGormUrls
    /**
     * Executable Drools Rule script
     */
    String rule

    @Override
    public String toString() {
        """
ValidationRule[
name: ${name},
identifiersToGormUrls: ${identifiersToGormUrls.toString()},
rule: ${rule}
]"""
    }
}
