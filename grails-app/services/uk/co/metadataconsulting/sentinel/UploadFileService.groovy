package uk.co.metadataconsulting.sentinel

import grails.plugin.awssdk.s3.AmazonS3Service
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.web.multipart.MultipartFile

@Slf4j
@CompileStatic
class UploadFileService {

    AmazonS3Service amazonS3Service

    UploadFileResult uploadFile(Long recordCollectionId, MultipartFile multipartFile) {
        if(!areAWSCredsAvailable()) {
            log.warn('You must provide either: Environment Variables - AWS_ACCESS_KEY_ID and AWS_SECRET_KEY or Java System Properties - aws.accessKeyId and aws.secretKey')
            return null
        }
        final String path = "${recordCollectionId}_${multipartFile.originalFilename}".toString()
        return new UploadFileResult(fileUrl: amazonS3Service.storeMultipartFile(path, multipartFile),
                path: path)
    }

    boolean deleteFile(String path) {
        if (path) {
            amazonS3Service.deleteFile(path)
        }
    }

    boolean areAWSCredsAvailable() {
        (System.getenv('AWS_ACCESS_KEY_ID') && System.getenv('AWS_SECRET_KEY')) ||
        (System.getProperty('aws.accessKeyId') && System.getProperty('aws.secretKey'))
    }
}