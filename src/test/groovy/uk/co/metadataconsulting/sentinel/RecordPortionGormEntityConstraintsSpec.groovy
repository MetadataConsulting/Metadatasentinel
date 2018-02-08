package uk.co.metadataconsulting.sentinel

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class RecordPortionGormEntityConstraintsSpec extends Specification implements DomainUnitTest<RecordPortionGormEntity> {

    void 'test name can be null'() {
        when:
        domain.name = null

        then:
        domain.validate(['name'])
    }

    void 'test gormUrl can be null'() {
        when:
        domain.gormUrl = null

        then:
        domain.validate(['gormUrl'])
    }

    void 'test value cannot be null'() {
        when:
        domain.value = null

        then:
        !domain.validate(['value'])
        domain.errors['value'].code == 'nullable'
    }

    void 'test valid cannot be null'() {
        when:
        domain.valid = null

        then:
        !domain.validate(['valid'])
        domain.errors['valid'].code == 'nullable'
    }
}
