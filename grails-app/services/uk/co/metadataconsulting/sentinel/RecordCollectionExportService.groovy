package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.sentinel.export.RecordCollectionExportRowView
import uk.co.metadataconsulting.sentinel.export.RecordCollectionExportView

@CompileStatic
class RecordCollectionExportService {

    RecordService recordService

    RecordGormService recordGormService

    RecordCollectionExportView export(Long recordCollectionId, RecordCorrectnessDropdown correctnes = RecordCorrectnessDropdown.VALID) {
        List<Long> recordIds = recordService.findAllIdsByRecordCollectionId(recordCollectionId, correctnes, null)
        List<RecordGormEntity> recordGormEntityList = recordGormService.findAllByIds(recordIds)
        List<RecordCollectionExportRowView> rows = []
        List<String> headers
        if ( recordGormEntityList ) {
            headers = recordGormEntityList.first().portions*.header
            for ( RecordGormEntity recordGormEntity : recordGormEntityList ) {
                List<RecordPortion> recordPortionList = recordGormEntity.portions.collect { RecordPortionGormEntity recordPortionGormEntity ->
                    RecordPortionUtils.of(recordPortionGormEntity) }
                rows << new RecordCollectionExportRowView(recordPortionList: recordPortionList)
            }
        }

        new RecordCollectionExportView(rows: rows, headers: headers)
    }
}