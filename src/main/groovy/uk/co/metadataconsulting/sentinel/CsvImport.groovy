package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
interface CsvImport {
    void save(InputStream inputStream, Integer batchSize, RecordCollectionMetadata recordCollectionMetadata)
}