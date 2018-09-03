package uk.co.metadataconsulting.monitor

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class CloneMappingCommandConstraintsSpec extends Specification {

    @Subject
    @Shared
    CloneMappingCommand cmd = new CloneMappingCommand()

    void 'test recordCollectionId cannot be null'() {
        when:
        cmd.fromRecordCollectionId = null

        then:
        !cmd.validate(['fromRecordCollectionId'])
        cmd.errors['fromRecordCollectionId'].code == 'nullable'
    }

    void 'test toRecordCollectionId cannot be null'() {
        when:
        cmd.toRecordCollectionId = null

        then:
        !cmd.validate(['toRecordCollectionId'])
        cmd.errors['toRecordCollectionId'].code == 'nullable'
    }
}
