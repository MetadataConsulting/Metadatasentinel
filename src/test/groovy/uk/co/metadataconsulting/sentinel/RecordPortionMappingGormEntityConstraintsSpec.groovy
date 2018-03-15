package uk.co.metadataconsulting.sentinel

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class RecordPortionMappingGormEntityConstraintsSpec extends Specification
        implements DomainUnitTest<RecordPortionMappingGormEntity> {

    void 'test header cannot be null'() {
        when:
        domain.header = null

        then:
        !domain.validate(['header'])
    }

    void 'test gormUrl can be null'() {
        when:
        domain.gormUrl = null

        then:
        domain.validate(['gormUrl'])
    }
}
