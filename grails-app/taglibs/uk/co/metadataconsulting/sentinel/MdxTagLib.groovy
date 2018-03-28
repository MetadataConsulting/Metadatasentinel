package uk.co.metadataconsulting.sentinel

import grails.config.Config
import grails.core.support.GrailsConfigurationAware

class MdxTagLib implements GrailsConfigurationAware {
    static namespace = 'mdx'

    public static final String GORM_PREFFIX = 'gorm'
    public static final String DOMAIN_CLASS_PACKAGE = 'org.modelcatalogue.core'

    String metadataUrl

    Integer maxHeader

    @Override
    void setConfiguration(Config co) {
        metadataUrl = co.getProperty('metadata.url', String, 'http://localhost:8080')
        maxHeader = co.getProperty('record.header.max', Integer, 30)
    }

    def link = { attrs, body ->
        RecordPortionGormEntity recordPortion = attrs.recordPortion
        RecordPortionMapping recordPortionMapping = attrs.recordPortionMappingList.find {
            it.header == recordPortion.header && it.gormUrl
        }
        if ( recordPortionMapping ) {
            out << "<a href='${linkFromGormUrl(recordPortionMapping.dataModelId, recordPortionMapping.gormUrl)}'>"
            out << header(recordPortion)
            out << "</a>"
        } else {
            out << "<span data-toggle='tooltip' title='${recordPortion.header}'>${header(recordPortion)}</span>"
        }

    }

    String header(RecordPortionGormEntity recordPortion) {
        if ( recordPortion.header.size() > maxHeader ) {
            return "${recordPortion.header.substring(0, Math.min(maxHeader, recordPortion.header.length()))}..."
        } else {
            return recordPortion.header
        }
    }

    String linkFromGormUrl(Long dataModelId, String gormUrl) {
        final String url = gormUrl.replaceAll("${GORM_PREFFIX}://${DOMAIN_CLASS_PACKAGE}.", "")
        final String[] arr = url.split(':')
        String lowerCamelCaseDomain = arr[0]
        lowerCamelCaseDomain = "${lowerCamelCaseDomain.substring(0, 1).toLowerCase()}${lowerCamelCaseDomain[1..-1]}"
        final Long domainId = arr[1] as Long
        "${metadataUrl}/#/${dataModelId}/${lowerCamelCaseDomain}/${domainId}"
    }
}
