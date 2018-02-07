package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
class RecordPortionController {

    static allowedMethods = [
            show: 'GET'
    ]

    RecordPortionGormService recordPortionGormService

    def show(Long recordId) {

        List<RecordPortionGormEntity> recordPortionList = recordPortionGormService.findAllByRecordId(recordId)
        Number recordPortionTotal = recordPortionGormService.countByRecordId(recordId)
        [
                recordPortionList: recordPortionList,
                recordPortionTotal: recordPortionTotal
        ]
    }
}