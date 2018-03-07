package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
enum ImportContentType {
    XSLX, CSV

    static ImportContentType of(String contentType) {
        if ( 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' == contentType ) {
            return XSLX
        }

        if ( 'text/csv' == contentType ) {
            return CSV
        }

        null
    }
}
