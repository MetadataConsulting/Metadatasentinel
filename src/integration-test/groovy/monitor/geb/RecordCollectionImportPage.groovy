package uk.co.metadataconsulting.monitor.geb

import geb.Page

class RecordCollectionImportPage extends Page {

    static url = '/import'

    static at = { title == 'Import File' }

    static content = {
        form { $('form#uploadForm') }
        submitButton { $('input', type: 'submit', 0) }
    }

    void submit() {
        submitButton.click()
    }

}
