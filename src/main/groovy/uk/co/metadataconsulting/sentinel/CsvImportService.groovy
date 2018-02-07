package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
interface CsvImport {
    void save(String mapping, InputStream inputStream, Integer batchSize)
}