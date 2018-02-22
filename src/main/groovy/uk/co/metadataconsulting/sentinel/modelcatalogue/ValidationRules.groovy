package uk.co.metadataconsulting.sentinel.modelcatalogue

import groovy.transform.CompileStatic
import org.modelcatalogue.core.scripting.Validating
import org.modelcatalogue.core.scripting.ValidatingImpl

@CompileStatic
class ValidationRules {

    String url
    String gormUrl
    String name
    List<ValidationRule> rules = []
    ValidatingImpl validating
}
