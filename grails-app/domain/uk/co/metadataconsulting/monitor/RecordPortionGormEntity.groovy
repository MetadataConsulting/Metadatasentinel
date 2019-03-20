package uk.co.metadataconsulting.monitor

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
/**
 * = A DataItem or Cell in a Spreadsheet.
 */
class RecordPortionGormEntity {

    String header
    String name
    String value
    ValidationStatus status = ValidationStatus.NOT_VALIDATED
    String reason
    Integer numberOfRulesValidatedAgainst
    Date lastUpdated

    static belongsTo = [record: RecordGormEntity]

    static constraints = {
        numberOfRulesValidatedAgainst min: 0, nullable: false
        header nullable: true, blank: true
        name nullable: true, blank: true
        value nullable: false, blank: false
        status nullable: false
        reason nullable: true
    }

    static mapping = {
        table 'recordportion'
        reason type: 'text'
        sort 'header'
    }

    @Override
    String toString() {
        """
[header: ${header},
name: ${name},
value: ${value},
status: ${status},
reason: ${reason},
numberOfRulesValidatedAgainst: ${numberOfRulesValidatedAgainst}]"""
    }
}