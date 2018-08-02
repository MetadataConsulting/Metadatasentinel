package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
interface CsvImport {
    RecordCollectionGormEntity save(InputStream inputStream, Integer batchSize, RecordCollectionMetadata recordCollectionMetadata)
}