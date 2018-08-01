package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import org.springframework.web.multipart.MultipartFile

@GrailsCompileStatic
class RecordFileCommand implements Validateable, RecordCollectionMetadata {
    MultipartFile csvFile
    Integer batchSize = 100
    String datasetName
    String about

    static constraints = {
        datasetName nullable: false, blank: false
        about nullable: true, blank: true
        batchSize nullable: false
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