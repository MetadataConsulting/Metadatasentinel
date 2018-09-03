package uk.co.metadataconsulting.monitor.export

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.monitor.ValidationStatus

@CompileStatic
class RecordCollectionExportView {
    List<String> headers = []
    List<RecordCollectionExportRowView> rows = []

    List<RecordCollectionExportRowView> findValidRows() {
        rows.findAll { RecordCollectionExportRowView row ->
            !row.recordPortionList.any { it.status == ValidationStatus.INVALID } && row.recordPortionList.any {
                it.status == ValidationStatus.VALID
            }
        }
    }

    List<RecordCollectionExportRowView> findInvalidRows() {
        rows.findAll { RecordCollectionExportRowView row ->
            row.recordPortionList.any { it.status == ValidationStatus.INVALID }
        }
    }

    List<RecordCollectionExportRowView> findNotValidatedRows() {
        rows.findAll { RecordCollectionExportRowView row ->
            row.recordPortionList.every { it.status == ValidationStatus.NOT_VALIDATED }
        }
    }

}
