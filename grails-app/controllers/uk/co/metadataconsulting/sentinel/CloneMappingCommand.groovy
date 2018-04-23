package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class CloneMappingCommand implements Validateable {
    Long fromRecordCollectionId
    Long toRecordCollectionId

    static constraints = {
        fromRecordCollectionId nullable: false
        toRecordCollectionId nullable: false
    }
}
