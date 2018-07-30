package uk.co.metadataconsulting.sentinel.geb

import geb.Module

class RecordCollectionRowModule extends Module {

    static content = {
        datasetnameCell { $('td', 0) }
        createdByCell { $('td', 1) }
        dateCreatedCell { $('td', 2) }
        updatedByCell { $('td', 3) }
        lastUpdatedCell { $('td', 4) }
        deleteInputSubmit { $('input', value: 'Delete', type: 'submit') }
    }

    String datesetname() {
        datasetnameCell.text()
    }

    String createdBy() {
        createdByCell.text()
    }

    String dateCreated() {
        dateCreatedCell.text()
    }

    String updatedBy() {
        updatedByCell.text()
    }

    String lastUpdated() {
        lastUpdatedCell.text()
    }

    void showRecordCollection() {
        datasetnameCell.$('a', 0).click()
    }

    void delete() {
        deleteInputSubmit.click()
    }
}
