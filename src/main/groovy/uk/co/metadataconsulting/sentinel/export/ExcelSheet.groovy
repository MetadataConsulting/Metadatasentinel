package uk.co.metadataconsulting.sentinel.export

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.sentinel.RecordPortion

@CompileStatic
class ExcelSheet {
    String sheetName
    List<String> headers
    List<RecordCollectionExportRowView> rows

    void setHeaders(List<String> headers) {
        this.headers = headers.sort()
    }
    void setRows(List<RecordCollectionExportRowView> rows) {
        this.rows = rows
        sortRecordPortionListForRows()
    }

    void sortRecordPortionListForRows() {
        for ( RecordCollectionExportRowView row : rows) {
            row.recordPortionList.sort { RecordPortion a, RecordPortion b ->
                a.header <=> b.header
            }
        }
    }
}
