package uk.co.metadataconsulting.sentinel

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class RecordIndexCommandConstraintsSpec extends Specification {

    @Subject
    @Shared
    RecordIndexCommand cmd = new RecordIndexCommand()

    void 'test recordCollectionId cannot be null'() {
        when:
        cmd.recordCollectionId = null

        then:
        !cmd.validate(['recordCollectionId'])
        cmd.errors['recordCollectionId'].code == 'nullable'
    }
}
