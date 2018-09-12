package uk.co.metadataconsulting.monitor

import spock.lang.Specification
import spock.lang.Unroll

class ImportContentTypeSpec extends Specification {

    @Unroll
    def "#extension of #contentType"(String extension, ImportContentType contentType) {
        expect:
        contentType == ImportContentType.of(extension)

        where:
        extension                                                           | contentType
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' | ImportContentType.XSLX
        'text/csv'                                                          | ImportContentType.CSV
    }
}
