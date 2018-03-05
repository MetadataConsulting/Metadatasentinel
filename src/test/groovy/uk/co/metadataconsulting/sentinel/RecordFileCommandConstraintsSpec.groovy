package uk.co.metadataconsulting.sentinel

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class RecordFileCommandConstraintsSpec extends Specification {

    @Subject
    @Shared
    RecordFileCommand cmd = new RecordFileCommand()

    void 'test mapping cannot be null'() {
        when:
        cmd.mapping = null

        then:
        !cmd.validate(['mapping'])
        cmd.errors['mapping'].code == 'nullable'
    }

    void 'test batchSize cannot be null'() {
        when:
        cmd.batchSize = null

        then:
        !cmd.validate(['batchSize'])
        cmd.errors['batchSize'].code == 'nullable'
    }
}
