package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import org.springframework.web.multipart.MultipartFile

@GrailsCompileStatic
class RecordFileCommand implements Validateable {
    MultipartFile csvFile
    String mapping
    Integer batchSize = 100

    static constraints = {
        batchSize nullable: false
        mapping nullable: false, blank: false
        csvFile  validator: { MultipartFile val, RecordFileCommand obj ->
            if ( val == null ) {
                return false
            }
            if ( val.empty ) {
                return false
            }
            allowedExtensions().any { String extension ->
                val.originalFilename?.toLowerCase()?.endsWith(extension)
            }
        }
    }

    static List<String> allowedExtensions() {
        ['csv', 'xlsx']
    }

}