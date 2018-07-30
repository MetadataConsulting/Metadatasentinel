package uk.co.metadataconsulting.sentinel

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class EditRecordCollectionCommandConstraintsSpec extends Specification {

    @Subject
    @Shared
    EditRecordCollectionCommand cmd = new EditRecordCollectionCommand()

    void 'test recordCollectionId cannot be null'() {
        when:
        cmd.recordCollectionId = null

        then:
        !cmd.validate(['recordCollectionId'])
        cmd.errors['recordCollectionId'].code == 'nullable'
    }
}
