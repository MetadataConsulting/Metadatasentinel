package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic

@CompileStatic
/**
 * Interface for services which import a CSV-like (tabular) data format.
 */
interface CsvImport {
    /**
     * Save a file in some format (InputStream) as the Records of a given (should be newly created) RecordCollection. A preliminary "default" mapping is made here.
     */
    void save(InputStream inputStream,
              Integer batchSize,
              RecordCollectionGormEntity recordCollectionEntity)
}