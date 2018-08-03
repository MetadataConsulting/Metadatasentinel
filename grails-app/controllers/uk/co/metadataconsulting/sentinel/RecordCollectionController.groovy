package uk.co.metadataconsulting.sentinel


import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.context.MessageSource
import uk.co.metadataconsulting.sentinel.export.CsvExportService
import uk.co.metadataconsulting.sentinel.export.ExcelExportService
import uk.co.metadataconsulting.sentinel.export.ExportFormat
import uk.co.metadataconsulting.sentinel.export.ExportRecordCollectionCommand
import uk.co.metadataconsulting.sentinel.export.ExportService
import uk.co.metadataconsulting.sentinel.export.RecordCollectionExportService
import uk.co.metadataconsulting.sentinel.export.RecordCollectionExportView
import uk.co.metadataconsulting.sentinel.modelcatalogue.DataModel
import uk.co.metadataconsulting.sentinel.modelcatalogue.GormUrlName
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules

import javax.servlet.ServletOutputStream

import static org.springframework.http.HttpStatus.OK

@CompileStatic
@Slf4j
class RecordCollectionController implements ValidateableErrorsMessage, GrailsConfigurationAware {

    static allowedMethods = [
            index: 'GET',
            edit: 'GET',
            importCsv: 'GET',
            uploadCsv: 'POST',
            validate: 'POST',
            delete: 'POST',
            headersMapping: 'GET',
            cloneMapping: 'GET',
            cloneSave: 'POST',
            export: 'GET',
            update: 'POST'
    ]

    MessageSource messageSource

    RecordCollectionGormService recordCollectionGormService

    RecordCollectionService recordCollectionService

    RecordCollectionMappingGormService recordCollectionMappingGormService

    RuleFetcherService ruleFetcherService

    RecordCollectionExportService recordCollectionExportService

    ExportService exportService

    CsvExportService csvExportService

    ExcelExportService excelExportService

    UpdateRecordCollectionService updateRecordCollectionService

    SaveRecordCollectionService saveRecordCollectionService

    CatalogueElementsService catalogueElementsService

    int defaultPaginationMax = 25
    int defaultPaginationOffset = 0
    String separator

    @Override
    void setConfiguration(Config co) {
        separator = co.getProperty('export.csv.separator', String, ',')
        defaultPaginationMax = co.getProperty('sentinel.pagination.max', Integer, 25)
        defaultPaginationOffset = co.getProperty('sentinel.pagination.offset', Integer, 0)
    }

    def index() {

        Integer max = params.int('max') ?: defaultPaginationMax
        Integer offset = params.int('offset') ?: defaultPaginationOffset
        PaginationQuery paginationQuery = new PaginationQuery(max: max, offset: offset)

        indexModel(paginationQuery)
    }

    def edit(EditRecordCollectionCommand cmd) {
        if ( cmd.hasErrors() ) {
            flash.error = errorsMsg(cmd, messageSource)
            redirect action: 'index'
            return
        }
        RecordCollectionGormEntity recordCollectionEntity = recordCollectionGormService.find(cmd.recordCollectionId)

        if (!recordCollectionEntity) {
            flash.error = messageSource.getMessage('recordCollection.notFound', [] as Object[], "Record collection not found", request.locale)
            redirect action: 'index'
            return
        }

        List<DataModel> dataModelList = ruleFetcherService.fetchDataModels()?.dataModels
        if ( !dataModelList ) {
            flash.error = messageSource.getMessage('dataModel.couldNotLoad', [] as Object[], 'Could not load data Models', request.locale)
        }
        [
                dataModelList: dataModelList,
                recordCollectionId: cmd.recordCollectionId,
                recordCollectionEntity: recordCollectionEntity,

        ]
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

    def update(UpdateRecordCollectionCommand cmd) {
        if ( cmd.hasErrors() ) {
            redirect(controller: 'recordCollection', action: 'index')
            return
        }
        List<DataModel> dataModelList = ruleFetcherService.fetchDataModels()?.dataModels
        DataModel dataModel = dataModelList?.find { it.id == cmd.dataModelId }
        if (!dataModel) {
            redirect(controller: 'recordCollection', action: 'edit', params: [recordCollectionId: cmd.recordCollectionId])
            return
        }
        updateRecordCollectionService.update(cmd)

        redirect controller: 'record',
                action: 'index',
                params: [recordCollectionId: cmd.recordCollectionId]
    }

    def export(ExportRecordCollectionCommand cmd) {

        if ( cmd.hasErrors() ) {
            redirect(controller: 'recordCollection', action: 'index')
            return
        }

        RecordCollectionGormEntity collectionGormEntity = recordCollectionGormService.find(cmd.recordCollectionId)
        if ( !collectionGormEntity ) {
            redirect(controller: 'recordCollection', action: 'index')
            return
        }

        RecordCorrectnessDropdown recordCorrectnessDropdown = RecordCorrectnessDropdown.ALL
        RecordCollectionExportView view = recordCollectionExportService.export(cmd.recordCollectionId, recordCorrectnessDropdown)

        response.status = OK.value()
        final String filename = "${collectionGormEntity.datasetName}.${exportService.fileExtensionForFormat(cmd.format)}"
        response.setHeader "Content-disposition", "attachment; filename=${filename}"
        response.contentType = exportService.mimeTypeForFormat(cmd.format)

        ServletOutputStream outs = response.outputStream
        switch (cmd.format) {
            case ExportFormat.CSV:
                csvExportService.export(outs, view)
                break
            case ExportFormat.XLSX_COMPACT:
            case ExportFormat.XLSX:
                excelExportService.export(outs, view, cmd.format, request.locale)
                break
        }

        outs.flush()
        outs.close()
    }

    def importCsv() {
        List dataModelList = ruleFetcherService.fetchDataModels()?.dataModels
        [dataModelList: dataModelList]
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
            List dataModelList = ruleFetcherService.fetchDataModels()?.dataModels
            render view: 'importCsv', model: [
                    dataModelList: dataModelList,
                    datasetName: cmd.datasetName,
                    dataModelId: cmd.dataModelId,
                    about: cmd.about,
            ]
            return
        }

        log.debug 'Content Type {}', cmd.csvFile.contentType
        RecordCollectionGormEntity recordCollectionGormEntity = saveRecordCollectionService.save(cmd)

        redirect controller: 'recordCollection', action: 'headersMapping', params: [recordCollectionId: recordCollectionGormEntity.id]
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
        RecordCollectionGormEntity recordCollectionGormEntity = recordCollectionGormService.find(recordCollectionId)
        if (!recordCollectionGormEntity) {
            redirect controller: 'recordCollection', action: 'index'
            return
        }
        List<GormUrlName> catalogueElementList = catalogueElementsService.findAllByDataModelId(recordCollectionGormEntity.dataModelId)

        List dataModelList = ruleFetcherService.fetchDataModels()?.dataModels
        if ( !dataModelList ) {
            flash.error = messageSource.getMessage('dataModel.couldNotLoad', [] as Object[], 'Could not load data Models', request.locale)
        }
        [
                catalogueElementList: catalogueElementList,
                recordCollectionEntity: recordCollectionGormEntity,
                dataModelList: dataModelList,
                recordCollectionId: recordCollectionId,
                recordPortionMappingList: recordCollectionMappingGormService.findAllByRecordCollectionId(recordCollectionId)
        ]
    }

    def cloneMapping(Long recordCollectionId) {
        RecordCollectionGormEntity recordCollectionEntity = recordCollectionGormService.find(recordCollectionId)
        Set<Long> recordCollectionIdList = recordCollectionMappingGormService.findAllRecordCollectionIdByGormUrlNotNull()
        [
                recordCollectionEntity: recordCollectionEntity,
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

}