package uk.co.metadataconsulting.sentinel

import builders.dsl.spreadsheet.api.Color
import builders.dsl.spreadsheet.builder.poi.PoiSpreadsheetBuilder
import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.util.logging.Slf4j
import org.apache.poi.xssf.usermodel.XSSFColor
import org.springframework.context.MessageSource
import uk.co.metadataconsulting.sentinel.export.ExportRecordCollectionCommand
import uk.co.metadataconsulting.sentinel.export.ExportService
import uk.co.metadataconsulting.sentinel.export.RecordCollectionExportRowView
import uk.co.metadataconsulting.sentinel.export.RecordCollectionExportService
import uk.co.metadataconsulting.sentinel.export.RecordCollectionExportView
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules

import java.awt.Color

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
        RecordCollectionExportView viewAll = recordCollectionExportService.export(cmd.recordCollectionId, recordCorrectnessDropdown)
        RecordCollectionExportView view2 = recordCollectionExportService.export(cmd.recordCollectionId, recordCorrectnessDropdown)
        RecordCollectionExportView view3 = recordCollectionExportService.export(cmd.recordCollectionId, recordCorrectnessDropdown)
        def outs = response.outputStream

        viewAll.rows = findAllValid(viewAll.rows)
        view2.rows = findAllInvalid(view2.rows)
        view3.rows = findAllNotValidated(view3.rows)

        List<String> headers = []

        //Ensure that all portions are sorted
        viewAll.rows.each { RecordCollectionExportRowView rowView ->
            List<RecordPortion> dataItemList = rowView.recordPortionList
            dataItemList.sort {
                it.header
            }
        }

        //Ensure that all portions are sorted
        view2.rows.each { RecordCollectionExportRowView rowView ->
            List<RecordPortion> dataItemList = rowView.recordPortionList
            dataItemList.sort {
                it.header
            }
        }

        //Ensure that all portions are sorted
        view3.rows.each { RecordCollectionExportRowView rowView ->
            List<RecordPortion> dataItemList = rowView.recordPortionList
            dataItemList.sort {
                it.header
            }
        }

        RecordCollectionExportRowView firstRow1 = viewAll.rows.first()
        viewAll.headers = firstRow1.recordPortionList*.header
        List<String> headers1 = viewAll.headers
        List<String> portionsHeaders1 = viewAll.rows.first().recordPortionList.collect { RecordPortion.toHeaderList() }.flatten()

        RecordCollectionExportRowView firstRow2 = view2.rows.first()
        view2.headers = firstRow2.recordPortionList*.header
        List<String> headers2 = view2.headers
        List<String> portionsHeaders2 = view2.rows.first().recordPortionList.collect { RecordPortion.toHeaderList() }.flatten()

        RecordCollectionExportRowView firstRow3 = view3.rows.first()
        view3.headers = firstRow3.recordPortionList*.header
        List<String> headers3 = view3.headers
        List<String> portionsHeaders3 = view3.rows.first().recordPortionList.collect { RecordPortion.toHeaderList() }.flatten()




        String filename = "${filenameprefix}.${exportService.fileExtensionForFormat(cmd.format)}"
        response.setHeader "Content-disposition", "attachment; filename=${filename}"
        response.contentType = exportService.mimeTypeForFormat(cmd.format)
        switch (cmd.format) {
            case ExportFormat.CSV:
                // TODO remove this line?
                headers = headers.collect { [it, it, it, it, it, it] }.flatten()
                outs << "${headers.join(separator)}\n"
                if ( viewAll.rows ) {
                    String line = "${portionsHeaders.join(separator)}\n"
                    outs << line
                    viewAll.rows.each { RecordCollectionExportRowView row ->
                        outs << "${row.toCsv(separator)}\n"
                    }
                }
                break
            case ExportFormat.XLSX:
                PoiSpreadsheetBuilder.create(outs).build {
                    sheet(messageSource.getMessage("export.valid".toString(), [] as Object[], 'Valid', request.locale)) {
                        row {
                            for ( String header : headers1 ) {
                                cell {
                                    value header
                                    colspan RecordPortion.toHeaderList().size()
                                }
                            }
                        }
                        if ( portionsHeaders1 ) {
                            row {
                                for ( String recordPortionHeader : portionsHeaders1 ) {
                                    cell {
                                        value messageSource.getMessage("export.header.${recordPortionHeader}".toString(),
                                                [] as Object[],
                                                recordPortionHeader,
                                                request.locale)
                                    }
                                }
                            }
                        }
                        if ( viewAll.rows ) {
                            //viewAll.rows.each{List<RecordCollectionExportRowView> rowView ->
                                for (RecordCollectionExportRowView rowView: viewAll.rows){
                                    row {
                                    for ( RecordPortion recordPortion : rowView.recordPortionList) {
                                        for (String val : recordPortion.toList()) {
                                            cell {
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
                            for ( String header : headers2 ) {
                                cell {
                                    value header
                                    colspan RecordPortion.toHeaderList().size()
                                }
                            }
                        }
                        if ( portionsHeaders1 ) {
                            row {
                                for ( String recordPortionHeader : portionsHeaders2 ) {
                                    cell {
                                        value messageSource.getMessage("export.header.${recordPortionHeader}".toString(),
                                                [] as Object[],
                                                recordPortionHeader,
                                                request.locale)
                                    }
                                }
                            }
                        }
                        if ( view2.rows ) {
                            for (RecordCollectionExportRowView rowView2  : view2.rows ) {
                                row {
                                    for ( RecordPortion recordPortion : rowView2.recordPortionList) {
                                        for (String val : recordPortion.toList()) {
                                            cell {
                                                value val
                                                style{

                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    sheet(messageSource.getMessage("export.notValidated".toString(), [] as Object[], 'Not Validated', request.locale)) {
                        row {
                            for ( String header : headers3 ) {
                                cell {
                                    value header
                                    colspan RecordPortion.toHeaderList().size()
                                }
                            }
                        }
                        if ( portionsHeaders3 ) {
                            row {
                                for ( String recordPortionHeader : portionsHeaders3) {
                                    cell {
                                        value messageSource.getMessage("export.header.${recordPortionHeader}".toString(),
                                                [] as Object[],
                                                recordPortionHeader,
                                                request.locale)
                                    }
                                }
                            }
                        }
                        if ( view3.rows ) {
                            for (RecordCollectionExportRowView rowView3 : view3.rows ) {
                                row {
                                    for ( RecordPortion recordPortion : rowView3.recordPortionList) {
                                        for (String val : recordPortion.toList()) {
                                            cell {
//                                                if (isInValidPortionFunction(recordPortion)) {
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
        List<RecordCollectionExportRowView>  newList =  all.each{ RecordCollectionExportRowView row ->

            def workingRecordPortionList = row.recordPortionList
            List<RecordPortion> validDataItemList = []
            workingRecordPortionList.each{ portion ->
                boolean bResult = isValidPortionFunction( portion)
                println "Is Valid:" + bResult + ":" + portion.toString()
                if(bResult){
                    validDataItemList.add(portion)
                    println "added portion"
                }else{
                    validDataItemList.add(new RecordPortion(portion.header))
                    println "empty portion added "
                }
            }
            row.recordPortionList = validDataItemList
        }
        return newList
    }
    private List<RecordCollectionExportRowView> findAllInvalid(List<RecordCollectionExportRowView> all) {
        List<RecordCollectionExportRowView>  newList =  all.each{ RecordCollectionExportRowView row ->

            def workingRecordPortionList = row.recordPortionList
            List<RecordPortion> validDataItemList = []
            workingRecordPortionList.each{ portion ->
                boolean bResult = isInValidPortionFunction(portion)
                println "Is In Valid:" + bResult + ":" + portion.toString()

                if(bResult){
                    validDataItemList.add(portion)
                    println "added portion"
                }else{
                    validDataItemList.add(new RecordPortion(portion.header))
                    println "empty portion added "
                }
            }
            row.recordPortionList = validDataItemList
        }
        return newList
    }
    private List<RecordCollectionExportRowView> findAllNotValidated(List<RecordCollectionExportRowView> all) {
        List<RecordCollectionExportRowView>  newList =  all.each{ RecordCollectionExportRowView row ->

            def workingRecordPortionList = row.recordPortionList
            List<RecordPortion> validDataItemList = []
            workingRecordPortionList.each{ portion ->
                boolean bResult = isNotYetValidatedPortionFunction(portion)
                println "Is Not Validated:" + bResult + ":" + portion.toString()
                if(bResult){
                    validDataItemList.add(portion)
                    println "added portion"
                }else{
                    validDataItemList.add(new RecordPortion(portion.header))
                    println "empty portion added "
                }
            }
            row.recordPortionList = validDataItemList
        }
        return newList
    }


    private boolean isValidPortionFunction(RecordPortion portion){
        ValidationStatus vStatus =  portion.status as ValidationStatus
        boolean bstatus = (vStatus == ValidationStatus.VALID)
        return bstatus
    }

    private boolean isInValidPortionFunction(RecordPortion portion){
        ValidationStatus vStatus =  portion.status as ValidationStatus
        boolean bstatus = (vStatus == ValidationStatus.INVALID)
        return bstatus
    }

    private boolean isNotYetValidatedPortionFunction(RecordPortion portion){
        ValidationStatus vStatus =  portion.status as ValidationStatus
        boolean bstatus = (vStatus == ValidationStatus.NOT_VALIDATED)
        return bstatus
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