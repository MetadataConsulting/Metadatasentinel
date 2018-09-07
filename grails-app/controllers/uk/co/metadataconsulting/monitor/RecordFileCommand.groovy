package uk.co.metadataconsulting.monitor

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import org.springframework.web.multipart.MultipartFile
import uk.co.metadataconsulting.monitor.validationTask.ValidationTaskFileCommand

@GrailsCompileStatic
class RecordFileCommand implements Validateable, RecordCollectionMetadata {
    MultipartFile csvFile
    Integer batchSize = 100
    String datasetName
    String about
    Long dataModelId

    static constraints = {
        datasetName nullable: false, blank: false
        about nullable: true, blank: true
        dataModelId nullable: false
        batchSize nullable: false
        csvFile  validator: { MultipartFile val, RecordFileCommand obj ->
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

    static RecordFileCommand of(ValidationTaskFileCommand validationTaskFileCommand) {
        return new RecordFileCommand(
                csvFile: validationTaskFileCommand.csvFile,
                batchSize: validationTaskFileCommand.batchSize,
                datasetName: validationTaskFileCommand.datasetName,
                about: validationTaskFileCommand.about,
                dataModelId: validationTaskFileCommand.dataModelId
        )
    }

}