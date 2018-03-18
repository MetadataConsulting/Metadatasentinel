package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class RecordCollectionMappingGormEntity {
    String header
    String gormUrl
    Long dataModelId
    Date dateCreated
    Date lastUpdated

    static belongsTo = [recordCollection: RecordCollectionGormEntity]

    static constraints = {
        header nullable: false
        gormUrl nullable: true
        dataModelId nullable: true
    }

    static mapping = {
        table 'recordcollectionmapping'
    }
}
