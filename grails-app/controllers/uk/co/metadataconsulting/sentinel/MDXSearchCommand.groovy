package uk.co.metadataconsulting.sentinel

import grails.validation.Validateable

class MDXSearchCommand implements Validateable {
    Long dataModelId
    String query
    boolean searchImports = false

    static constraints = {
        dataModelId nullable: false
        query nullable: false
    }
}