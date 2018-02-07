package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
interface CsvImportService {
    void save(Long recordCollectionId, String mapping, InputStream inputStream, Integer batchSize)
}