package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic

@CompileStatic
/**
 * Partially represents a Record as a list of gormUrls and values.
 * gormUrls[i] and values[i] should correspond to each other.
 * gormUrls[i] is the GORM URL of the MDX Catalogue Element, associated with the column-header,
 * under which is the cell, from which values[i] came.
 */
class RecordGormUrlsAndValues {
    List<String> gormUrls
    List<String> values
}
