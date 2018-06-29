package uk.co.metadataconsulting.sentinel.export

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.sentinel.RecordPortion

@CompileStatic
class RecordCollectionExportRowView {
    List<RecordPortion> recordPortionList

    String toCsv(String separator = ';') {
        recordPortionList.collect { it.toCsv(separator) }.join(separator)
    }

    List<String> toList() {
        recordPortionList.collect { it.toList() }.flatten() as List<String>
    }
}
