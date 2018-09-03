package uk.co.metadataconsulting.monitor

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
/**
 * = A DataSet or a Spreadsheet. A tabular set of data.
 */
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

    String fileKey

    static hasMany = [
            mappings: RecordCollectionMappingEntryGormEntity,
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
        fileKey nullable: true
    }

    static mapping = {
        table 'recordcollection'
        about type: 'text'
    }
}