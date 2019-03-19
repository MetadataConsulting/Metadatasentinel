package uk.co.metadataconsulting.monitor

import grails.validation.Validateable

class UpdateRecordCollectionCommand implements Validateable, RecordCollectionMetadata {
    Long recordCollectionId
    String datasetName
    Long dataModelId
    String about

    static constraints = {
        recordCollectionId nullable: false
        dataModelId nullable: false
        datasetName nullable: true, blank: true
        about nullable: true, blank: true
    }
}