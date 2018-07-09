package uk.co.metadataconsulting.sentinel.export

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import uk.co.metadataconsulting.sentinel.export.ExportFormat

@GrailsCompileStatic
class ExportRecordCollectionCommand implements Validateable {

    Long recordCollectionId
    ExportFormat format = ExportFormat.XLSX

    static constraints = {
        recordCollectionId nullable: false
    }
}