package uk.co.metadataconsulting.sentinel

import groovy.util.logging.Slf4j
import org.springframework.context.MessageSource
import uk.co.metadataconsulting.sentinel.export.RecordCollectionExportRowView
import uk.co.metadataconsulting.sentinel.export.RecordCollectionExportView

//import pl.touk.excel.export.WebXlsxExporter
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules
import grails.config.Config
import grails.core.support.GrailsConfigurationAware

import static org.springframework.http.HttpStatus.OK

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
            exportValidRecords: 'GET'
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

    int defaultPaginationMax = 25
    int defaultPaginationOffset = 0
    String csvMimeType
    String encoding

    @Override
    void setConfiguration(Config co) {
        defaultPaginationMax = co.getProperty('sentinel.pagination.max', Integer, 25)
        defaultPaginationOffset = co.getProperty('sentinel.pagination.offset', Integer, 0)
        csvMimeType = co.getProperty('grails.mime.types.csv', String, 'text/csv')
        encoding = co.getProperty('grails.converters.encoding', String, 'UTF-8')
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

    def exportValidRecords(Long recordCollectionId){

         final String filename = 'dataset_valid.csv'

        def outs = response.outputStream
        response.status = OK.value()
        response.contentType = "${csvMimeType};charset=${encoding}"
        response.setHeader "Content-disposition", "attachment; filename=${filename}"
        final String separator = ';'
        RecordCollectionExportView view = recordCollectionExportService.export(recordCollectionId)
        List<String> headers = view.headers
        // TODO remove this line
        headers = headers.collect { [it, it, it, it, it, it] }.flatten()
        outs << "${headers.join(separator)}\n"

        if ( view.rows ) {
            List<String> portionsHeaders = view.rows.first().recordPortionList.collect { RecordPortion.toHeaderCsv(separator) }
            String line = "${portionsHeaders.join(separator)}\n"
            outs << line
            view.rows.each { RecordCollectionExportRowView row ->
                outs << "${row.toCsv(separator)}\n"
            }
        }

        outs.flush()
        outs.close()
    }

    def exportValidExcel(Long recordCollectionId) {

        String csvMimeType
        String encoding

        final String filename = 'dataset_valid.csv'
        //List<RecordPortionMapping> recordPortionMappingList = recordCollectionMappingGormService.findAllByRecordCollectionId(recordCollectionId)
        List<RecordGormEntity> recordGormEntityList = recordGormService.findAllByRecordCollectionId(recordCollectionId)
        List<RecordGormEntity> dataRecordsList = new ArrayList<RecordGormEntity>()
        List<String> headers = new ArrayList<String>()
        recordGormEntityList.each { RecordGormEntity record ->
            record.portions.each { RecordPortionGormEntity portion ->
                headers.add(portion.header)
            }
        }

       // new XlsxExporter('/tmp/myReportFile.xlsx').
       //         add(dataRecordsList, properties).save()
//        new WebXlsxExporter().with {
//            setResponseHeaders(response)
//            fillHeader(headers)
//            add(dataRecordsList )
//            save(response.outputStream)
//        }


        redirect action: 'index', controller: 'record', params: [recordCollectionId: recordCollectionId]
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