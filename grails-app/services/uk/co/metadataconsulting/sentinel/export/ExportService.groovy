package uk.co.metadataconsulting.sentinel.export

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileStatic
import uk.co.metadataconsulting.sentinel.export.ExportFormat

@CompileStatic
class ExportService implements GrailsConfigurationAware {
    String xlsxMimeType
    String xlsMimeType
    String csvMimeType
    String encoding

    @Override
    void setConfiguration(Config co) {
        csvMimeType = co.getProperty('grails.mime.types.csv', String, 'text/csv')
        xlsMimeType = co.getProperty('grails.mime.types.xlsMimeType', String,'application/vnd.ms-excel')
        xlsxMimeType = co.getProperty('grails.mime.types.xlsxMimeType', String, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet')
        encoding = co.getProperty('grails.converters.encoding', String, 'UTF-8')
    }

    String fileExtensionForFormat(ExportFormat format) {
        switch (format) {
            case ExportFormat.CSV:
                return 'csv'

            case ExportFormat.XLSX:
                return "xlsx"
        }
    }
    String mimeTypeForFormat(ExportFormat format) {
        switch (format) {
            case ExportFormat.CSV:
                return "${csvMimeType};charset=${encoding}"

            case ExportFormat.XLSX:
                return "${xlsxMimeType};charset=${encoding}"
        }
    }
}