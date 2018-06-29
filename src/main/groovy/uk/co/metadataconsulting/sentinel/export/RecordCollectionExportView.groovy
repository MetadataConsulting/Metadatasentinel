package uk.co.metadataconsulting.sentinel.export

import groovy.transform.CompileStatic

@CompileStatic
class RecordCollectionExportView {
    List<String> headers = []
    List<RecordCollectionExportRowView> rows = []
}
