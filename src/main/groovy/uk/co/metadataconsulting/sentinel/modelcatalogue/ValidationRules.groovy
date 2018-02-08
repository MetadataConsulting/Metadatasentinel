package uk.co.metadataconsulting.sentinel.modelcatalogue

import groovy.transform.CompileStatic

@CompileStatic
class ValidationRules {

    String gormUrl
    String name

    List<ValidationRule> rules = []
}
