package uk.co.metadataconsulting.sentinel

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class RecordCollectionGormEntityConstraintsSpec extends Specification implements DomainUnitTest<RecordCollectionGormEntity> {

    void 'verify datasetName is required and cannot be null'() {
        when:
        domain.datasetName = null

        then:
        !domain.validate(['datasetName'])

        when:
        domain.datasetName = ''

        then:
        !domain.validate(['datasetName'])
    }

}
