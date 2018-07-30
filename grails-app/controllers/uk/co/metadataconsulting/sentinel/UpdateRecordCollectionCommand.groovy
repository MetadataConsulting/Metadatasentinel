package uk.co.metadataconsulting.sentinel

import grails.validation.Validateable

class UpdateRecordCollectionCommand implements Validateable {
    Long recordCollectionId
    Long dataModelId

    static constraints = {
        recordCollectionId nullable: false
        dataModelId nullable: false
    }
}