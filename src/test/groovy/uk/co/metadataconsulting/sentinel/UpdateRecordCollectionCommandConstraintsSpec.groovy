package uk.co.metadataconsulting.sentinel

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class UpdateRecordCollectionCommandConstraintsSpec extends Specification {

    @Subject
    @Shared
    UpdateRecordCollectionCommand cmd = new UpdateRecordCollectionCommand()

    void 'test recordCollectionId cannot be null'() {
        when:
        cmd.recordCollectionId = null

        then:
        !cmd.validate(['recordCollectionId'])
        cmd.errors['recordCollectionId'].code == 'nullable'
    }

    void 'test dataModelId cannot be null'() {
        when:
        cmd.dataModelId = null

        then:
        !cmd.validate(['dataModelId'])
        cmd.errors['dataModelId'].code == 'nullable'
    }
}
