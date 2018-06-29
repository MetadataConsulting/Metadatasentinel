package uk.co.metadataconsulting.sentinel

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class RecordFileCommandConstraintsSpec extends Specification {

    @Subject
    @Shared
    RecordFileCommand cmd = new RecordFileCommand()

    void 'test batchSize cannot be null'() {
        when:
        cmd.batchSize = null

        then:
        !cmd.validate(['batchSize'])
        cmd.errors['batchSize'].code == 'nullable'
    }

    void 'verify datasetName is required and cannot be null'() {
        when:
        cmd.datasetName = null

        then:
        !cmd.validate(['datasetName'])

        when:
        cmd.datasetName = ''

        then:
        !cmd.validate(['datasetName'])
    }
}
