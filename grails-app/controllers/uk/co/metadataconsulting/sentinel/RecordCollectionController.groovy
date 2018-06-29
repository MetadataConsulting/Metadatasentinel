package uk.co.metadataconsulting.sentinel

import builders.dsl.spreadsheet.api.Color
import builders.dsl.spreadsheet.builder.poi.PoiSpreadsheetBuilder
import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.util.logging.Slf4j
import org.springframework.context.MessageSource
import uk.co.metadataconsulting.sentinel.export.ExportRecordCollectionCommand
import uk.co.metadataconsulting.sentinel.export.ExportService
import uk.co.metadataconsulting.sentinel.export.RecordCollectionExportRowView
import uk.co.metadataconsulting.sentinel.export.RecordCollectionExportService
import uk.co.metadataconsulting.sentinel.export.RecordCollectionExportView
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules
import static org.springframework.http.HttpStatus.OK
import  uk.co.metadataconsulting.sentinel.export.ExportFormat

@Slf4j
class RecordCollectionController implements ValidateableErrorsMessage, GrailsConfigurationAware {

    static allowedMethods = [
            index: 'GET',
            importCsv: 'GET',
            uploadCsv: 'POST',
            validate: 'POST',
            delete: 'POST',
            headersMapping: 'GET',
            cloneMapping: 'GET',
            cloneSave: 'POST',
            export: 'GET'
    ]

    MessageSource messageSource

    CsvImportService csvImportService

    RecordCollectionGormService recordCollectionGormService

    RecordGormService recordGormService

    RecordCollectionService recordCollectionService

    ExcelImportService excelImportService

    RecordCollectionMappingGormService recordCollectionMappingGormService

    RuleFetcherService ruleFetcherService

    RecordCollectionExportService recordCollectionExportService

    ExportService exportService

    int defaultPaginationMax = 25
    int defaultPaginationOffset = 0
    String separator

    @Override
    void setConfiguration(Config co) {
        separator = co.getProperty('export.csv.separator', String, ';')
        defaultPaginationMax = co.getProperty('sentinel.pagination.max', Integer, 25)
        defaultPaginationOffset = co.getProperty('sentinel.pagination.offset', Integer, 0)
    }

    def index() {

        Integer max = params.int('max') ?: defaultPaginationMax
        Integer offset = params.int('offset') ?: defaultPaginationOffset
        PaginationQuery paginationQuery = new PaginationQuery(max: max, offset: offset)

        indexModel(paginationQuery)
    }

    protected PaginationQuery paginationQueryWithDefaultSettings() {
        new PaginationQuery(max: defaultPaginationMax, offset: defaultPaginationOffset)
    }

    protected Map indexModel(PaginationQuery paginationQuery) {
        List<RecordCollectionGormEntity> recordCollectionList = recordCollectionGormService.findAll(paginationQuery.toMap())
        Number recordCollectionListTotal = recordCollectionGormService.count()
        [
                recordCollectionList: recordCollectionList,
                paginationQuery: paginationQuery,
                recordCollectionTotal: recordCollectionListTotal,
        ]
    }

    def export(ExportRecordCollectionCommand cmd) {

        if ( cmd.hasErrors() ) {
            // TODO Maybe display an error message with flash.error and redirect to HomePage
            return
        }

        RecordCollectionGormEntity collectionGormEntity = recordCollectionGormService.find(cmd.recordCollectionId)
        if ( !collectionGormEntity ) {
            // TODO Maybe display an error message with flash.error and redirect to HomePage
            return
        }
        final String filenameprefix = collectionGormEntity.datasetName
        response.status = OK.value()

        RecordCorrectnessDropdown recordCorrectnessDropdown = RecordCorrectnessDropdown.ALL
        RecordCollectionExportView view = recordCollectionExportService.export(cmd.recordCollectionId, recordCorrectnessDropdown)
        def outs = response.outputStream
        List<String> headers = view.headers
        List<String> portionsHeaders = view.rows.first().recordPortionList.collect { RecordPortion.toHeaderList() }.flatten()
        String filename = "${filenameprefix}.${exportService.fileExtensionForFormat(cmd.format)}"
        response.setHeader "Content-disposition", "attachment; filename=${filename}"
        response.contentType = exportService.mimeTypeForFormat(cmd.format)
        switch (cmd.format) {
            case ExportFormat.CSV:
                // TODO remove this line?
                headers = headers.collect { [it, it, it, it, it, it] }.flatten()
                outs << "${headers.join(separator)}\n"
                if ( view.rows ) {
                    String line = "${portionsHeaders.join(separator)}\n"
                    outs << line
                    view.rows.each { RecordCollectionExportRowView row ->
                        outs << "${row.toCsv(separator)}\n"
                    }
                }
                break
            case ExportFormat.XLSX:
                PoiSpreadsheetBuilder.create(outs).build {
                    sheet(messageSource.getMessage("export.valid".toString(), [] as Object[], 'Valid', request.locale)) {
                        row {
                            for ( String header : headers ) {
                                cell {
                                    value header
                                    colspan RecordPortion.toHeaderList().size()
                                }
                            }
                        }
                        if ( portionsHeaders ) {
                            row {
                                for ( String recordPortionHeader : portionsHeaders ) {
                                    cell {
                                        value messageSource.getMessage("export.header.${recordPortionHeader}".toString(),
                                                [] as Object[],
                                                recordPortionHeader,
                                                request.locale)
                                    }
                                }
                            }
                        }
                        if ( view.rows ) {
                            for (RecordCollectionExportRowView rowView  : findAllValid(view.rows) ) {
                                row {
                                    for ( RecordPortion recordPortion : rowView.recordPortionList) {
                                        for (String val : recordPortion.toList()) {
                                            cell {
//                                                if (recordPortion.status == ValidationStatus.INVALID) {
//                                                    style {
//                                                        font {
//                                                            color red
//                                                        }
//                                                    }
//                                                }
                                                value val
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    sheet(messageSource.getMessage("export.invalid".toString(), [] as Object[], 'Invalid', request.locale)) {
                        row {
                            for ( String header : headers ) {
                                cell {
                                    value header
                                    colspan RecordPortion.toHeaderList().size()
                                }
                            }
                        }
                        if ( portionsHeaders ) {
                            row {
                                for ( String recordPortionHeader : portionsHeaders ) {
                                    cell {
                                        value messageSource.getMessage("export.header.${recordPortionHeader}".toString(),
                                                [] as Object[],
                                                recordPortionHeader,
                                                request.locale)
                                    }
                                }
                            }
                        }
                        if ( view.rows ) {
                            for (RecordCollectionExportRowView rowView  : findAllInvalid(view.rows) ) {
                                row {
                                    for ( RecordPortion recordPortion : rowView.recordPortionList) {
                                        for (String val : recordPortion.toList()) {
                                            cell {
//                                                if (recordPortion.status == ValidationStatus.INVALID) {
//                                                    style {
//                                                        font {
//                                                            color red
//                                                        }
//                                                    }
//                                                }
                                                value val
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    sheet(messageSource.getMessage("export.notValidated".toString(), [] as Object[], 'Not Validated', request.locale)) {
                        row {
                            for ( String header : headers ) {
                                cell {
                                    value header
                                    colspan RecordPortion.toHeaderList().size()
                                }
                            }
                        }
                        if ( portionsHeaders ) {
                            row {
                                for ( String recordPortionHeader : portionsHeaders ) {
                                    cell {
                                        value messageSource.getMessage("export.header.${recordPortionHeader}".toString(),
                                                [] as Object[],
                                                recordPortionHeader,
                                                request.locale)
                                    }
                                }
                            }
                        }
                        if ( view.rows ) {
                            for (RecordCollectionExportRowView rowView  : findAllNotValidated(view.rows) ) {
                                row {
                                    for ( RecordPortion recordPortion : rowView.recordPortionList) {
                                        for (String val : recordPortion.toList()) {
                                            cell {
//                                                if (recordPortion.status == ValidationStatus.INVALID) {
//                                                    style {
//                                                        font {
//                                                            color red
//                                                        }
//                                                    }
//                                                }
                                                value val
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                break
        }
        outs.flush()
        outs.close()
    }

    private List<RecordCollectionExportRowView> findAllValid(List<RecordCollectionExportRowView> all) {
        all.findAll { RecordCollectionExportRowView row ->
            !row.recordPortionList.any { it.status == ValidationStatus.INVALID } && row.recordPortionList.any { it.status == ValidationStatus.VALID }
        }
    }

    private List<RecordCollectionExportRowView> findAllInvalid(List<RecordCollectionExportRowView> all) {
        all.findAll { RecordCollectionExportRowView row ->
            row.recordPortionList.any { it.status == ValidationStatus.INVALID }
        }
    }

    private List<RecordCollectionExportRowView> findAllNotValidated(List<RecordCollectionExportRowView> all) {
        all.findAll { RecordCollectionExportRowView row ->
            row.recordPortionList.every { it.status == ValidationStatus.NOT_VALIDATED }
        }
    }

    def importCsv() {
        [:]
    }

    def validate(Long recordCollectionId) {

        List<RecordPortionMapping> recordPortionMappingList = recordCollectionMappingGormService.findAllByRecordCollectionId(recordCollectionId)
        Map<String, ValidationRules> validationRulesMap = ruleFetcherService.fetchValidationRulesByMapping(recordPortionMappingList)

        if ( !validationRulesMap ) {
            flash.error = messageSource.getMessage('record.validation.noRules', [] as Object[],'Could not trigger validation. No rules for mapping', request.locale)
            render view: 'index', model: indexModel(paginationQueryWithDefaultSettings())
            return
        }

        recordCollectionService.validate(recordCollectionId, recordPortionMappingList, validationRulesMap)
        flash.message = messageSource.getMessage('record.validation', [] as Object[],'Record Collection validation triggered', request.locale)
        redirect action: 'index', controller: 'record', params: [recordCollectionId: recordCollectionId]
    }

    def uploadCsv(RecordFileCommand cmd) {
        if ( cmd.hasErrors() ) {
            flash.error = errorsMsg(cmd, messageSource)
            render view: 'importCsv'
            return
        }

        log.debug 'Content Type {}', cmd.csvFile.contentType
        InputStream inputStream = cmd.csvFile.inputStream
        Integer batchSize = cmd.batchSize
        String datasetName = cmd.datasetName
        CsvImport importService = csvImportByContentType (ImportContentType.of(cmd.csvFile.contentType))
        importService.save(inputStream, datasetName, batchSize)

        redirect controller: 'recordCollection', action: 'index'
    }

    def delete(Long recordCollectionId) {

        if ( !recordCollectionId ) {
            redirect controller: 'recordCollection', action: 'index'
            return
        }

        recordCollectionGormService.delete(recordCollectionId)
        flash.message = messageSource.getMessage('recordCollection.deleted', [] as Object[],'Record Collection deleted', request.locale)

        redirect controller: 'recordCollection', action: 'index'
    }

    def headersMapping(Long recordCollectionId) {
        List dataModelList = ruleFetcherService.fetchDataModels()?.dataModels
        if ( !dataModelList ) {
            flash.error = messageSource.getMessage('dataModel.couldNotLoad', [] as Object[], 'Could not load data Models', request.locale)
        }
        [
                dataModelList: dataModelList,
                recordCollectionId: recordCollectionId,
                recordPortionMappingList: recordCollectionMappingGormService.findAllByRecordCollectionId(recordCollectionId)
        ]
    }

    def cloneMapping(Long recordCollectionId) {
        Set<Long> recordCollectionIdList = recordCollectionMappingGormService.findAllRecordCollectionIdByGormUrlNotNull()
        [
                toRecordCollectionId: recordCollectionId,
                recordCollectionList: recordCollectionGormService.findAllInIds(recordCollectionIdList)
        ]
    }

    def cloneSave(CloneMappingCommand cmd) {
        if ( cmd.hasErrors() ) {
            flash.error = errorsMsg(cmd, messageSource)
            redirect action: 'cloneMapping'
            return
        }

        recordCollectionMappingGormService.cloneMapping(cmd.fromRecordCollectionId, cmd.toRecordCollectionId)

        redirect action: 'headersMapping', params: [recordCollectionId: cmd.toRecordCollectionId]
    }

    protected CsvImport csvImportByContentType(ImportContentType contentType) {
        if ( contentType == ImportContentType.XSLX ) {
            return excelImportService
        }
        if ( contentType == ImportContentType.CSV ) {
            return csvImportService
        }
        null
    }
}