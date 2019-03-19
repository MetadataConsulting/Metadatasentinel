package uk.co.metadataconsulting.monitor.geb

import geb.Module

class RecordCollectionRowModule extends Module {

    static content = {
        datasetnameCell { $('td', 0) }
        dataModelNameCell { $('td', 1) }
        createdByCell { $('td', 2) }
        dateCreatedCell { $('td', 3) }
        updatedByCell { $('td', 4) }
        lastUpdatedCell { $('td', 5) }
        deleteInputSubmit { $('input', value: 'Delete', type: 'submit') }
    }

    String dataModelName() {
        dataModelNameCell.text()
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
