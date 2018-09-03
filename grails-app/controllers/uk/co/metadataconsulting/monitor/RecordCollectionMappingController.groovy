package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic
import org.springframework.context.MessageSource
import uk.co.metadataconsulting.monitor.modelcatalogue.GormUrlName

@CompileStatic
class RecordCollectionMappingController {

    static allowedMethods = [
            update: 'POST',
            catalogueElements: 'GET',
            saveMapping: 'GET'
    ]

    MessageSource messageSource
    RecordCollectionMappingGormService recordCollectionMappingGormService
    CatalogueElementsService catalogueElementsService
    ReconciliationService reconciliationService
    RecordPortionGormService recordPortionGormService

    def update() {
        Long recordCollectionId = params.long('recordCollectionId')
        List<Long> recordPortionMappingIds = recordCollectionMappingGormService.findIdsByRecordCollectionId(recordCollectionId)
        if ( recordPortionMappingIds ) {
            List<UpdateGormUrlRequest> cmds = []
            for ( Long id : recordPortionMappingIds ) {
                String gormUrl = params.getProperty("catalogueElement${id}")
                Long dataModelId = params.long("dataModel${id}")
                if ( gormUrl && gormUrl != 'null' && dataModelId ) {
                    cmds << new UpdateGormUrlRequest(id: id, gormUrl: gormUrl, dataModelId: dataModelId)
                }
            }
            recordCollectionMappingGormService.updateGormUrls(cmds)
            flash.message = messageSource.getMessage('recordCollection.mapping.updated', [] as Object[],'Record Collection Mapping updated', request.locale)
        }
        redirect uri: "/recordCollection/$recordCollectionId/mapping".toString()
    }

    def saveMapping(SaveMappingCommand cmd) {
        if(cmd.hasErrors()) {
            response.status = 422
            return
        }
        RecordCollectionMappingGormEntity recordCollectionMappingEntity =
                recordCollectionMappingGormService.updateGormUrlName(cmd.recordPortionMappingId,
                                                                 cmd.gormUrl, cmd.name)
        [recordCollectionMappingEntity: recordCollectionMappingEntity]
    }

    def catalogueElements(CatalogueElementsCommand cmd) {
        if(cmd.hasErrors()) {
            response.status = 400
            return
        }
        List<GormUrlName> catalogueElementList = catalogueElementsService.findAllByDataModelId(cmd.dataModelId)
        if ( cmd.query ) {
            Map<String, List<GormUrlName>> suggestions = reconciliationService.reconcile(catalogueElementList, [cmd.query], cmd.threshold)
            catalogueElementList = suggestions.values().flatten() as List<GormUrlName>
        }

        [catalogueElements: catalogueElementList]
    }
}