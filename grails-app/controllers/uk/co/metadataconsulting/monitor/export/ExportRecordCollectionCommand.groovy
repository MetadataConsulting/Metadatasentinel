package uk.co.metadataconsulting.monitor.export

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class ExportRecordCollectionCommand implements Validateable {

    Long recordCollectionId
    ExportFormat format = ExportFormat.XLSX

    static constraints = {
        recordCollectionId nullable: false
    }
}