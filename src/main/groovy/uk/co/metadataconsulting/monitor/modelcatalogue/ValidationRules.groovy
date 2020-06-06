package uk.co.metadataconsulting.monitor.modelcatalogue

import groovy.transform.CompileStatic
import org.modelcatalogue.core.scripting.ValidatingImpl

@CompileStatic
/**
 * An MDX CatalogueElement with A. an associated set of ValidationRules (used in Drools Engine),
 * plus B. a "ValidatingImpl" (another kind of rule... Groovy script)
 */
class ValidationRules {

    /**
     * URL of MDX CatalogueElement
     */
    String url
    /**
     * GORM URL of MDX CatalogueElement
     */
    String gormUrl
    /**
     * Name of MDX CatalogueElement
     */
    String name
    /**
     * Associated ValidationRules (Drools Rules)
     */
    List<ValidationRule> rules = []
    /**
     * Another kind of validation rule. A Groovy Script.
     */
    ValidatingImpl validating

    @Override
    String toString() {
        String rulesString = rules.collect{it.toString()}.join(',\n')
        """
ValidationRules[url: ${url},
gormUrl: ${gormUrl},
name: ${name},
rules: [${rulesString}],
validating: ${validating.toString()}
"""
    }
}
