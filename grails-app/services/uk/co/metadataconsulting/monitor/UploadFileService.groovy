package uk.co.metadataconsulting.monitor

import grails.config.Config
import grails.core.GrailsApplication
import grails.core.support.GrailsConfigurationAware
import grails.plugin.awssdk.s3.AmazonS3Service
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.web.multipart.MultipartFile

import javax.annotation.PostConstruct

@Slf4j
@CompileStatic
class UploadFileService implements GrailsConfigurationAware {

    GrailsApplication grailsApplication

    boolean enabled = true

    @PostConstruct
    void initialize() {
        if (!areAWSCredsAvailable()) {
            enabled = false
            log.warn('UploadService disabled. You must provide either: Environment Variables - AWS_ACCESS_KEY_ID and AWS_SECRET_KEY or Java System Properties - aws.accessKeyId and aws.secretKey')
        }
    }

    UploadFileResult uploadFile(Long recordCollectionId, MultipartFile multipartFile) {
        if (!enabled) {
            log.warn("UploadFile service not enabled")
            return null
        }
        final String path = "${recordCollectionId}_${multipartFile.originalFilename}".toString()
        Object bean = grailsApplication.mainContext.getBean("amazonS3Service")
        if (bean instanceof AmazonS3Service) {
            AmazonS3Service amazonS3Service = (AmazonS3Service) bean
            log.info("saving ${multipartFile.originalFilename} to S3")
            String fileUrl = amazonS3Service.storeMultipartFile(path, multipartFile)
            return new UploadFileResult(fileUrl: fileUrl, path: path)
        }
        return null

    }

    boolean deleteFile(String path) {
        if (!enabled) {
            return false
        }
        if (path) {
            Object bean = grailsApplication.mainContext.getBean("amazonS3Service")
            if (bean instanceof AmazonS3Service) {
                AmazonS3Service amazonS3Service = (AmazonS3Service) bean
                return amazonS3Service.deleteFile(path)
            }
        }
        return false
    }

    boolean areAWSCredsAvailable() {
        (System.getenv('AWS_ACCESS_KEY_ID') && System.getenv('AWS_SECRET_KEY')) ||
        (System.getProperty('aws.accessKeyId') && System.getProperty('aws.secretKey'))
    }

    @Override
    void setConfiguration(Config co) {
        String region = co.getProperty('grails.plugins.awssdk.s3.region', String)
        if (region == '${S3_REGION}') {
            enabled = false
            log.warn('UploadService disabled. You must provide a value for grails.plugins.awssdk.s3.region. Set it in application.yml or provide a S3_REGION environment variable')
        }
        String bucket = co.getProperty('grails.plugins.awssdk.s3.bucket', String)
        if (bucket == '${S3_BUCKET}') {
            enabled = false
            log.warn('UploadService disabled. You must provide a value for grails.plugins.awssdk.s3.bucket. Set it in application.yml or provide a S3_BUCKET environment variable')
        }
    }
}