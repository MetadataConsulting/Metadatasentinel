package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.monitor.modelcatalogue.ValidationRules

@CompileStatic
class MappingMetadata {
    List<String> headerLineList = []
    List<String> gormUrls = []
    Map<String, ValidationRules> gormUrlsRules = [:]
}

