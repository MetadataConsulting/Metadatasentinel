package uk.co.metadataconsulting.monitor

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
/**
 * = A DataEntry or Row in a Spreadsheet.
 */
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