package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules

@CompileStatic
class MappingMetadata {
    List<String> headerLineList = []
    List<String> gormUrls = []
    Map<String, ValidationRules> gormUrlsRules = [:]
}

