package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class RecordCollectionGormEntity {

    String datasetName

    Date dateCreated

    Date lastUpdated

    String createdBy

    String updatedBy

    String dataModelName

    Long dataModelId

    static hasMany = [
            mappings: RecordCollectionMappingGormEntity,
            records: RecordGormEntity
    ]

    static constraints = {
        records nullable: true
        mappings nullable: true
        datasetName nullable: false, blank: false
        createdBy nullable: true
        updatedBy nullable: true
        dataModelId nullable: true
        dataModelName nullable: true
    }

    static mapping = {
        table 'recordcollection'
    }
}