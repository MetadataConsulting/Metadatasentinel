package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class RecordCollectionGormEntity {

    Date dateCreated

    Date lastUpdated

    static hasOne = [recordCollectionMapping: RecordCollectionMappingGormEntity]

    static hasMany = [records: RecordGormEntity]

    static constraints = {
        records nullable: true
        recordCollectionMapping unique: true
    }

    static mapping = {
        table 'recordcollection'
    }
}