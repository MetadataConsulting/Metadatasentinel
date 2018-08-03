package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class RecordCollectionGormEntity {

    String datasetName

    String about

    Date dateCreated

    Date lastUpdated

    String createdBy

    String updatedBy

    String dataModelName

    Long dataModelId

    String fileUrl

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
        about nullable: true, blank: true
        fileUrl nullable: true
    }

    static mapping = {
        table 'recordcollection'
        about type: 'text'
    }
}