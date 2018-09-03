package uk.co.metadataconsulting.monitor.export

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class ExportRecordCollectionCommandConstraintsSpec extends Specification {

    @Subject
    @Shared
    ExportRecordCollectionCommand cmd = new ExportRecordCollectionCommand()

    void 'test recordCollectionId cannot be null'() {
        when:
        cmd.recordCollectionId = null

        then:
        !cmd.validate(['recordCollectionId'])
        cmd.errors['recordCollectionId'].code == 'nullable'
    }
}
