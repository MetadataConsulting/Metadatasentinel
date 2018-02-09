package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class RecordIndexCommand implements Validateable {

    Integer offset
    Integer max
    Long recordCollectionId
    RecordCorrectnessDropdown correctness = RecordCorrectnessDropdown.ALL

    static constraints = {
        offset min: 0, nullable: true
        max min: 0, nullable: true
        recordCollectionId nullable: false
        correctness nullable: false
    }

    PaginationQuery toPaginationQuery() {
        new PaginationQuery(
                max: max ?: 10,
                offset: offset ?: 0
        )
    }
}