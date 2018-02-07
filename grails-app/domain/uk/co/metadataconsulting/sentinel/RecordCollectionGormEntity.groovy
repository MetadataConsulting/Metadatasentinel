package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class RecordCollectionGormEntity {

    Date dateCreated

    Date lastUpdated

    static hasMany = [records: RecordGormEntity]

    static constraints = {
        records nullable: true
    }

    static mapping = {
        table 'recordcollection'
    }
}