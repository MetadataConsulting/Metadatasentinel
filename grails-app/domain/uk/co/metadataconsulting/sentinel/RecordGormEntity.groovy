package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class RecordGormEntity {

    Date dateCreated

    String createdBy

    String updatedBy

    static belongsTo = [recordCollection: RecordCollectionGormEntity]

    static hasMany = [portions: RecordPortionGormEntity]

    static mapping = {
        table 'record'
        nullable: false
    }

    static constraints = {
        createdBy nullable: true
        updatedBy nullable: true
    }
}