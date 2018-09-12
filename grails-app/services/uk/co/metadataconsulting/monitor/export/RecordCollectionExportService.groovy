package uk.co.metadataconsulting.monitor.export

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.monitor.RecordCorrectnessDropdown
import uk.co.metadataconsulting.monitor.RecordGormEntity
import uk.co.metadataconsulting.monitor.RecordGormService
import uk.co.metadataconsulting.monitor.RecordPortion
import uk.co.metadataconsulting.monitor.RecordPortionGormEntity
import uk.co.metadataconsulting.monitor.RecordPortionUtils
import uk.co.metadataconsulting.monitor.RecordService

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