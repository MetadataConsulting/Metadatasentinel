package uk.co.metadataconsulting.monitor.validationTask

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import org.springframework.web.multipart.MultipartFile
import uk.co.metadataconsulting.monitor.RecordCollectionMetadata

@GrailsCompileStatic
class ValidationTaskFileCommand implements Validateable, RecordCollectionMetadata {
    MultipartFile csvFile
    Long validationTaskId
    Integer batchSize = 100
    String datasetName
    String about
    Long dataModelId

    static constraints = {
        datasetName nullable: false, blank: false
        about nullable: true, blank: true
        validationTaskId nullable: true
        dataModelId nullable: false
        batchSize nullable: false
        csvFile  validator: { MultipartFile val, ValidationTaskFileCommand obj ->
            if ( val == null ) {
                return 'nofile'
            }
            if ( val.empty ) {
                return 'nofile'
            }
            if (!allowedExtensions().any { String extension ->
                val.originalFilename?.toLowerCase()?.endsWith(extension)
            }) {
                return 'invalidextension'
            }
            return true
        }
    }

    static List<String> allowedExtensions() {
        ['csv', 'xlsx']
    }

}