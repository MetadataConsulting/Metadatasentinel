package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class RecordPortionGormEntity {

    String header
    String name
    String url
    String value
    Boolean valid
    String reason
    Integer numberOfRulesValidatedAgainst
    Date lastUpdated

    static belongsTo = [record: RecordGormEntity]

    static constraints = {
        numberOfRulesValidatedAgainst min: 0, nullable: false
        url nullable: true
        header nullable: true, blank: true
        name nullable: true, blank: true
        value nullable: false, blank: false
        valid nullable: false, blank: false
        reason nullable: true
    }

    static mapping = {
        table 'recordportion'
        reason type: 'text'
    }
}