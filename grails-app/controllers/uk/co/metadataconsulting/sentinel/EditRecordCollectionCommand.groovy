package uk.co.metadataconsulting.sentinel

import grails.validation.Validateable

class EditRecordCollectionCommand implements Validateable {
    Long recordCollectionId

    static constraints = {
        recordCollectionId nullable: false
    }
}