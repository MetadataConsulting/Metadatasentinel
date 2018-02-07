package uk.co.metadataconsulting.sentinel

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class RecordPortionGormEntityConstraintsSpec extends Specification implements DomainUnitTest<RecordPortionGormEntity> {

    void 'test name cannot be null'() {
        when:
        domain.name = null

        then:
        !domain.validate(['name'])
        domain.errors['name'].code == 'nullable'
    }

    void 'test metatadataDomainEntity cannot be null'() {
        when:
        domain.metatadataDomainEntity = null

        then:
        !domain.validate(['metatadataDomainEntity'])
        domain.errors['metatadataDomainEntity'].code == 'nullable'
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
