package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class RecordPortionGormEntity {

    String name
    String metatadataDomainEntity
    String value
    Boolean valid
    String reason

    static belongsTo = [record: RecordGormEntity]

    static constraints = {
        metatadataDomainEntity nullable: false, blank: false
        name nullable: false, blank: false
        value nullable: false, blank: false
        valid nullable: false, blank: false
        reason nullable: true
    }

    static mapping = {
        table 'recordportion'
        reason type: 'text'
    }
}