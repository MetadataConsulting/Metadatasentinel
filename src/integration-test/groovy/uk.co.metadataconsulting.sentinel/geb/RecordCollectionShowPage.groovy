package uk.co.metadataconsulting.sentinel.geb

import geb.Page
import geb.module.Select
import uk.co.metadataconsulting.sentinel.export.ExportFormat

class RecordCollectionShowPage extends Page {

    static url = '/record/index'

    static content = {
        exportInputSubmit { $('input', value: 'Export', type: 'submit') }
        exportDropdownLink { $('a#export') }
        dropdownLink { $('.dropdown-menu a', text: it) }
        validateButton { $(type: 'submit', value: 'Validate')}
        deleteButton { $(type: 'submit', value: 'Delete')}
        infoDiv { $('div.alert-info', 0) }
        createdBySpan { $('#createdBy', 0) }
        updatedBySpan { $('#updatedBy', 0) }
    }

    void delete() {
        withConfirm(true) {
            deleteButton.click()
        }
    }

    String alertInfo() {
        infoDiv.text()
    }

    String createdBy() {
        createdBySpan.text()
    }

    String updatedBy() {
        updatedBySpan.text()
    }


    static at = {
        title == 'Records'
    }

    void validate() {
        validateButton.click()
    }

    @Override
    String convertToPath(Object[] args) {
        args ? '?recordCollectionId=' + args[0].toString() : ""
    }

    void export(ExportFormat exportFormat = ExportFormat.XLSX_COMPACT) {
        exportDropdownLink.click()
        waitFor {
            dropdownLink(exportFormat.toString()).click()
        }
    }
}
