package uk.co.metadataconsulting.monitor.export

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.monitor.RecordPortion
import uk.co.metadataconsulting.monitor.ValidationStatus

@CompileStatic
class CsvExportService {

    void export(OutputStream outs, RecordCollectionExportView view, String separator = ';') {
        outs << "${view.headers.join(separator)}\n"
        for (RecordCollectionExportRowView rowView : view.rows) {
            List<String> l = rowView.recordPortionList.collect { RecordPortion recordPortion ->
                if (recordPortion.status == ValidationStatus.INVALID) {
                    return "(${recordPortion.value})".toString()
                }
                "${recordPortion.value}".toString()
            }
            outs << "${l.join(separator)}\n"
        }
    }
}