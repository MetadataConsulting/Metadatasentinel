package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
interface CsvImportProcessor {
    int processInputStream(InputStream inputStream, Integer batchSize, Closure headerListClosure, Closure cls)
}
