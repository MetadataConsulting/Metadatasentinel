package uk.co.metadataconsulting.sentinel.geb

import geb.Page

class RecordCollectionIndexPage extends Page {
    static url = '/'

    static at = { title == 'Record Collections' }

    static content = {
        importFileLink { $('#importfile-link') }
    }

    void importFile() {
        importFileLink.click()
    }
}
