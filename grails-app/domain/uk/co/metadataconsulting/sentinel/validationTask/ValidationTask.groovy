package uk.co.metadataconsulting.sentinel.validationTask


import grails.compiler.GrailsCompileStatic
import uk.co.metadataconsulting.sentinel.RecordCollectionGormEntity

@GrailsCompileStatic
class ValidationTask {
    String name
    Date dateCreated
    Date lastUpdated
    List validationPasses

    static hasMany = [
            validationPasses: ValidationPass
    ]
}