package uk.co.metadataconsulting.sentinel.modelcatalogue

import groovy.transform.Canonical
import groovy.transform.CompileStatic

@Canonical
@CompileStatic
class ValidationRule {

    String name
    List<String> gormUrls
    String rule
}
