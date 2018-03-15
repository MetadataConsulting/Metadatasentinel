package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class RecordCollectionMappingGormEntity {
    Date dateCreated
    Date lastUpdated

    static belongsTo = [recordCollection: RecordCollectionGormEntity]
    static hasMany = [recordPortionMappings: RecordPortionMappingGormEntity]

    static constraints = {
        recordPortionMappings nullable: false, minSize: 1
    }

    static mapping = {
        table 'recordcollectionmapping'
    }
}
