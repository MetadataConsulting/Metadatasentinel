package uk.co.metadataconsulting.sentinel

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class RecordPortionGormEntityConstraintsSpec extends Specification implements DomainUnitTest<RecordPortionGormEntity> {

    void 'test header can be null'() {
        when:
        domain.header = null

        then:
        domain.validate(['header'])
    }

    void 'test name can be null'() {
        when:
        domain.name = null

        then:
        domain.validate(['name'])
    }

    void 'test value cannot be null'() {
        when:
        domain.value = null

        then:
        !domain.validate(['value'])
        domain.errors['value'].code == 'nullable'
    }

    void 'test status cannot be null'() {
        when:
        domain.status = null

        then:
        !domain.validate(['status'])
        domain.errors['status'].code == 'nullable'
    }

    void 'test numberOfRulesValidatedAgainst cannot be null'() {
        when:
        domain.numberOfRulesValidatedAgainst = null

        then:
        !domain.validate(['numberOfRulesValidatedAgainst'])
        domain.errors['numberOfRulesValidatedAgainst'].code == 'nullable'
    }

    void 'test numberOfRulesValidatedAgainst min is 0'() {
        when:
        domain.numberOfRulesValidatedAgainst = -1

        then:
        !domain.validate(['numberOfRulesValidatedAgainst'])
        domain.errors['numberOfRulesValidatedAgainst'].code == 'min.notmet'
    }

    void 'test reason can be null'() {
        when:
        domain.reason = null

        then:
        domain.validate(['reason'])
    }
}
