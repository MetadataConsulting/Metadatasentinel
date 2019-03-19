package uk.co.metadataconsulting.monitor

import grails.validation.Validateable

class EditRecordCollectionCommand implements Validateable {
    Long recordCollectionId

    static constraints = {
        recordCollectionId nullable: false
    }
}