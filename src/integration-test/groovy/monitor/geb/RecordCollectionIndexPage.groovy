package uk.co.metadataconsulting.monitor.geb

import geb.Page

class RecordCollectionIndexPage extends Page {
    static url = '/'

    static at = { title == 'Record Collections' }

    static content = {
        importFileLink { $('#importfile-link') }
        rows(required: false) { $('table tbody tr').moduleList(RecordCollectionRowModule) }
    }

    void importFile() {
        importFileLink.click()
    }
}
