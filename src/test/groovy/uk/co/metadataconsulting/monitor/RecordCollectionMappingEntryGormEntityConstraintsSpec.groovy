package uk.co.metadataconsulting.monitor

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class RecordCollectionMappingEntryGormEntityConstraintsSpec extends Specification
        implements DomainUnitTest<RecordCollectionMappingEntryGormEntity> {

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
