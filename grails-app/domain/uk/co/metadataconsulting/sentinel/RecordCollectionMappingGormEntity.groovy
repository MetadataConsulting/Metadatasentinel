package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class RecordCollectionMappingGormEntity {
    String header
    String gormUrl
    Date dateCreated
    Date lastUpdated

    static belongsTo = [recordCollection: RecordCollectionGormEntity]

    static constraints = {
        header nullable: false
        gormUrl nullable: true
    }

    static mapping = {
        table 'recordcollectionmapping'
    }
}
