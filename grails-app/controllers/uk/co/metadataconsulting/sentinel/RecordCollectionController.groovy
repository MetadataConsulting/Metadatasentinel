package uk.co.metadataconsulting.sentinel

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.context.MessageSource
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules

@Slf4j
@CompileStatic
class RecordCollectionController implements ValidateableErrorsMessage, GrailsConfigurationAware {

    static allowedMethods = [
            index: 'GET',
            importCsv: 'GET',
            uploadCsv: 'POST',
            validate: 'POST',
            delete: 'POST',
            headersMapping: 'GET'
    ]

    MessageSource messageSource

    CsvImportService csvImportService

    RecordCollectionGormService recordCollectionGormService

    RecordCollectionService recordCollectionService

    ExcelImportService excelImportService

    RecordCollectionMappingGormService recordCollectionMappingGormService

    RuleFetcherService ruleFetcherService

    int defaultPaginationMax = 25
    int defaultPaginationOffset = 0

    @Override
    void setConfiguration(Config co) {
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
        CsvImport importService = csvImportByContentType (ImportContentType.of(cmd.csvFile.contentType))
        importService.save(inputStream, batchSize)

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
        [
                dataModelList: ruleFetcherService.fetchDataModels()?.dataModels,
                recordCollectionId: recordCollectionId,
                recordPortionMappingList: recordCollectionMappingGormService.findAllByRecordCollectionId(recordCollectionId)
        ]
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