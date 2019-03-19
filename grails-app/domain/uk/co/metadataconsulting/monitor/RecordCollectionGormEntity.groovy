package uk.co.metadataconsulting.monitor

import grails.compiler.GrailsCompileStatic
import uk.co.metadataconsulting.monitor.validationTask.ValidationPass

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

    List<String> headersList

    static hasMany = [
            mappings: RecordCollectionMappingEntryGormEntity,
            records: RecordGormEntity
    ]

    static belongsTo = [validationPass: ValidationPass]

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
