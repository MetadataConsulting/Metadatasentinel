package uk.co.metadataconsulting.monitor

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

    void 'test datasetName can be null'() {
        when:
        cmd.datasetName = null

        then:
        cmd.validate(['datasetName'])
    }

    void 'test about can be null'() {
        when:
        cmd.about = null

        then:
        cmd.validate(['about'])
    }
}
