package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import org.springframework.web.multipart.MultipartFile

@GrailsCompileStatic
class RecordCsvCommand implements Validateable {
    MultipartFile csvFile
    String mapping
    Integer batchSize = 100

    static constraints = {
        batchSize nullable: false
        mapping nullable: false, blank: false
        csvFile  validator: { MultipartFile val, RecordCsvCommand obj ->
            if ( val == null ) {
                return false
            }
            if ( val.empty ) {
                return false
            }
            val.originalFilename?.toLowerCase()?.endsWith('csv')
        }
    }
}