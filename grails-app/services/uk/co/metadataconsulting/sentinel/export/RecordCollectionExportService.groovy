package uk.co.metadataconsulting.sentinel.export

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.sentinel.RecordCorrectnessDropdown
import uk.co.metadataconsulting.sentinel.RecordGormEntity
import uk.co.metadataconsulting.sentinel.RecordGormService
import uk.co.metadataconsulting.sentinel.RecordPortion
import uk.co.metadataconsulting.sentinel.RecordPortionGormEntity
import uk.co.metadataconsulting.sentinel.RecordPortionUtils
import uk.co.metadataconsulting.sentinel.RecordService
import uk.co.metadataconsulting.sentinel.export.RecordCollectionExportRowView
import uk.co.metadataconsulting.sentinel.export.RecordCollectionExportView

@CompileStatic
class RecordCollectionExportService {

    RecordService recordService

    RecordGormService recordGormService

    RecordCollectionExportView export(Long recordCollectionId, RecordCorrectnessDropdown correctness = RecordCorrectnessDropdown.VALID) {
        List<Long> recordIds = recordService.findAllIdsByRecordCollectionId(recordCollectionId, correctness, null)
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