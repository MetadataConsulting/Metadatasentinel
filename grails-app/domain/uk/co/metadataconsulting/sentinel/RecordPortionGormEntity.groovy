package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class RecordPortionGormEntity {

    String name
    String gormUrl
    String value
    Boolean valid
    String reason

    static belongsTo = [record: RecordGormEntity]

    static constraints = {
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