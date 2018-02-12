package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class RecordPortionGormEntity {

    String name
    String gormUrl
    String value
    Boolean valid
    String reason
    Integer numberOfRulesValidatedAgainst

    static belongsTo = [record: RecordGormEntity]

    static constraints = {
        numberOfRulesValidatedAgainst min: 0, nullable: false
        gormUrl nullable: true, blank: true
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