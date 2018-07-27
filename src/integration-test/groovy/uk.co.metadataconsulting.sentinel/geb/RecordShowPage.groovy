package uk.co.metadataconsulting.sentinel.geb

import geb.Page

class RecordShowPage extends Page {

    static url = "/records"

    static content = {
        validateButton { $(type: 'submit', value: 'Validate')}
    }

    static at = {
        title == 'Record Portions'
    }

    void validate() {
        validateButton.click()
    }

    @Override
    String convertToPath(Object[] args) {
        args ? '/' + args*.toString().join('/') : ""
    }
}
