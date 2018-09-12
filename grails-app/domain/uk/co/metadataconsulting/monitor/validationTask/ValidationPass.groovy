package uk.co.metadataconsulting.monitor.validationTask

import grails.compiler.GrailsCompileStatic
import uk.co.metadataconsulting.monitor.RecordCollectionGormEntity

@GrailsCompileStatic
class ValidationPass {
    /**
     * Which pass is this, the 1st, 2nd...
     */
    Long position

    /**
     * A ValidationPass belongs to a ValidationTask
     */
    static belongsTo = [validationTask: ValidationTask]

    RecordCollectionGormEntity recordCollection

}