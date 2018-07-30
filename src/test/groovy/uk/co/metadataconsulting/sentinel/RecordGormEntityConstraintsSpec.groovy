package uk.co.metadataconsulting.sentinel

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class RecordGormEntityConstraintsSpec extends Specification implements DomainUnitTest<RecordGormEntity> {

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
