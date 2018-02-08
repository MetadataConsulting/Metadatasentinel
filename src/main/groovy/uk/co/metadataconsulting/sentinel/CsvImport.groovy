package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
interface CsvImport {
    void save(List<String> gormUrls, InputStream inputStream, Integer batchSize)
}