package uk.co.metadataconsulting.sentinel

import grails.testing.web.UrlMappingsUnitTest
import spock.lang.Specification

class UrlMappingsSpec extends Specification implements UrlMappingsUnitTest<UrlMappings> {
    void setup() {
        mockController(RecordCollectionController)
        mockController(RecordController)
    }

    void "test forward mappings"() {
        expect:
        verifyForwardUrlMapping("/", controller: 'recordCollection', action: 'index')
        verifyForwardUrlMapping("/recordCollection/export", controller: 'recordCollection', action: 'export')
        verifyForwardUrlMapping("/recordCollection/cloneMapping", controller: 'recordCollection', action: 'cloneMapping')
        verifyForwardUrlMapping("/recordCollection/validate", controller: 'recordCollection', action: 'validate')
        verifyForwardUrlMapping("/recordCollection/delete", controller: 'recordCollection', action: 'delete')
        verifyForwardUrlMapping("/recordCollection/1/mapping", controller: 'recordCollection', action: 'headersMapping') {
            recordCollectionId = '1'
        }
        verifyForwardUrlMapping("/import", controller: 'recordCollection', action: 'importCsv')
        verifyForwardUrlMapping("/upload", controller: 'recordCollection', action: 'uploadCsv')
        verifyForwardUrlMapping("/records/1", controller: 'record', action: 'index') {
            recordCollectionId = '1'
        }
        verifyForwardUrlMapping("/records/1/2", controller: 'record', action: 'show') {
            recordCollectionId = '1'
            recordId = '2'
        }
        verifyForwardUrlMapping("/record/validate", controller: 'record', action: 'validate')
        verifyForwardUrlMapping("/record/show", controller: 'record', action: 'show')
    }
}
