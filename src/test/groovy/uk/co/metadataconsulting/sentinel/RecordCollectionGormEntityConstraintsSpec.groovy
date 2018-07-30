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

    void 'verify dataModelName can be null'() {
        when:
        domain.dataModelName = null

        then:
        domain.validate(['dataModelName'])
    }

    void 'verify dataModelId can be null'() {
        when:
        domain.dataModelId = null

        then:
        domain.validate(['dataModelId'])
    }

    void 'verify createdBy can be null'() {
        when:
        domain.createdBy = null

        then:
        domain.validate(['createdBy'])
    }

    void 'verify updatedBy can be null'() {
        when:
        domain.updatedBy = null

        then:
        domain.validate(['updatedBy'])
    }
}
