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

    void 'test offset can be null'() {
        when:
        cmd.offset = null

        then:
        cmd.validate(['offset'])
    }

    void 'test max can be null'() {
        when:
        cmd.offset = null

        then:
        cmd.validate(['offset'])
    }

    void 'default correctness is ALL'() {
        expect:
        cmd.correctness == RecordCorrectnessDropdown.ALL
    }

    void 'returns PaginationQuery built with max and offset'() {
        when:
        cmd.max = 15
        cmd.offset = 10
        PaginationQuery paginationQuery = cmd.toPaginationQuery()

        then:
        paginationQuery
        paginationQuery.max == 15
        paginationQuery.offset == 10

    }
}
