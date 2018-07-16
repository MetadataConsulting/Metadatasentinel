package uk.co.metadataconsulting.sentinel.geb

import geb.Module

class RecordCollectionRowModule extends Module {

    static content = {
        nameCell { $('td', 0) }
        creationDateCell { $('td', 1) }
        deleteInputSubmit { $('input', value: 'Delete', type: 'submit') }
    }

    void showRecordCollection() {
        nameCell.$('a', 0).click()
    }

    void delete() {
        deleteInputSubmit.click()
    }
}
