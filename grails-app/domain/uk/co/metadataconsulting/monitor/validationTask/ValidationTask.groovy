package uk.co.metadataconsulting.monitor.validationTask


import grails.compiler.GrailsCompileStatic

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