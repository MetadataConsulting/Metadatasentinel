package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class RecordGormEntity {

    Date dateCreated

    static belongsTo = [recordCollection: RecordCollectionGormEntity]

    static hasMany = [portions: RecordPortionGormEntity]

    static mapping = {
        table 'record'
        nullable: false
    }
}