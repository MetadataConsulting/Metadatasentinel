package uk.co.metadataconsulting.monitor.validationTask

import grails.compiler.GrailsCompileStatic
import uk.co.metadataconsulting.monitor.RecordCollectionGormEntity

@GrailsCompileStatic
class ValidationPass {
    Date dateCreated
    Date lastUpdated
    /**
     * A ValidationPass belongs to a ValidationTask
     */
    static belongsTo = [validationTask: ValidationTask]

    RecordCollectionGormEntity recordCollection

}