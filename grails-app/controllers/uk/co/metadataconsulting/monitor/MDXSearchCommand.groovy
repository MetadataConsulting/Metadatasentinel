package uk.co.metadataconsulting.monitor

import grails.validation.Validateable

class MDXSearchCommand implements Validateable {
    Long dataModelId
    String query
    boolean searchImports = false
    boolean fuzzy = false

    static constraints = {
        dataModelId nullable: false
        query nullable: false
    }
}