package uk.co.metadataconsulting.monitor

import grails.validation.Validateable

class CatalogueElementsCommand implements Validateable {
    Long dataModelId
    String query
    Float threshold = null

    static constraints = {
        dataModelId nullable: false
        query nullable: true
        threshold nullable: true
    }
}