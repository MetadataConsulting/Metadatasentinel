package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.monitor.modelcatalogue.ValidationRules

@CompileStatic
/**
 * Contains metadata for the validation of a record collection.
 */
class MappingMetadata {
    /**
     * List of headers
     */
    List<String> headersList = []
    /**
     * List of gormUrls corresponding to the headers pointwise.
     */
    List<String> gormUrls = []
    /**
     * Map from gormUrls to ValidationRules.
     */
    Map<String, ValidationRules> gormUrlsRules = [:]
}

