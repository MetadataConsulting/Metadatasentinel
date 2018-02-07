package uk.co.metadataconsulting.sentinel

import grails.testing.web.UrlMappingsUnitTest
import spock.lang.Specification

class UrlMappingsSpec extends Specification implements UrlMappingsUnitTest<UrlMappings> {
    void setup() {
        mockController(RecordCollectionController)
        mockController(RecordController)
        mockController(RecordPortionController)
    }

    void "test forward mappings"() {
        expect:
        verifyForwardUrlMapping("/", controller: 'recordCollection', action: 'index')
        verifyForwardUrlMapping("/import", controller: 'recordCollection', action: 'importCsv')
        verifyForwardUrlMapping("/upload", controller: 'recordCollection', action: 'uploadCsv')
        verifyForwardUrlMapping("/records/1", controller: 'record', action: 'index') {
            recordCollectionId = '1'
        }
        verifyForwardUrlMapping("/records/1/2", controller: 'recordPortion', action: 'show') {
            recordCollectionId = '1'
            recordId = '2'
        }
    }
}
