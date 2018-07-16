package uk.co.metadataconsulting.sentinel.geb

import geb.Page
import geb.module.Select
import uk.co.metadataconsulting.sentinel.export.ExportFormat

class RecordCollectionShowPage extends Page {

    static url = '/record'

    static content = {
        exportInputSubmit { $('input', value: 'Export', type: 'submit') }
        exportFormatSelect { $('select', name: 'format').module(Select) }
    }

    @Override
    String convertToPath(Object[] args) {
        args ? '/' + args*.toString().join('/') : ""
    }

    void export(ExportFormat exportFormat = ExportFormat.XLSX_COMPACT) {
        exportFormatSelect.setSelected(exportFormat.toString())
        exportInputSubmit.click()
    }
}
