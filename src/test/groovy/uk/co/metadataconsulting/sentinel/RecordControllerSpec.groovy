package uk.co.metadataconsulting.sentinel

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

import static javax.servlet.http.HttpServletResponse.SC_OK

class RecordControllerSpec extends Specification implements ControllerUnitTest<RecordController> {

    void "invoking uploadCsv with invalid parameters renders importCsv again"() {
        when:
        request.method = 'GET'
        controller.index()

        then:
        response.status == SC_OK
        flash.error != null
        view == '/record/index'
    }

    def "RecordController.index model contains fields recordCollectionId, invalidRecordTotal, allRecordTotal, recordList, paginationQuery"() {
        given:
        controller.recordService = Mock(RecordService)
        controller.recordCollectionGormService = Mock(RecordCollectionGormService)

        when:
        params.recordCollectionId = 1
        request.method = 'GET'
        Map model = controller.index()

        then:
        response.status == SC_OK
        model
        model.containsKey(field)

        where:
        field << ['recordCollectionId', 'invalidRecordTotal','allRecordTotal', 'recordList', 'paginationQuery' ]
    }

    def "RecordController.recordTotal model contains field paginationQuery"() {
        given:
        controller.recordService = Mock(RecordService)
        controller.recordCollectionGormService = Mock(RecordCollectionGormService)

        when:
        params.recordCollectionId = 1
        request.method = 'GET'
        Map model = controller.index()

        then:
        response.status == SC_OK
        model
        model.containsKey('recordTotal')
    }

    def "RecordController.show model contains fields recordPortionList, recordId, recordPortionTotal"() {
        given:
        controller.recordPortionGormService = Mock(RecordPortionGormService)
        controller.recordCollectionMappingGormService = Mock(RecordCollectionMappingGormService)
        controller.recordCollectionGormService = Mock(RecordCollectionGormService)

        when:
        params.recordId = 1
        request.method = 'GET'
        Map model = controller.show()

        then:
        response.status == SC_OK
        model
        model.containsKey(field)

        where:
        field << ['recordPortionList', 'recordId', 'recordPortionTotal']
    }


    def "RecordController.index model contains recordCollectionEntity"() {
        given:
        controller.recordPortionGormService = Mock(RecordPortionGormService)
        controller.recordCollectionMappingGormService = Mock(RecordCollectionMappingGormService)
        controller.recordService = Stub(RecordService) {
            countByRecordCollectionIdAndCorrectness() >> 1
        }
        controller.recordCollectionGormService = Stub(RecordCollectionGormService) {
            find(_) >> new RecordCollectionGormEntity()
        }

        when:
        params.recordCollectionId = 1
        request.method = 'GET'
        Map model = controller.index()

        then:
        response.status == SC_OK
        model
        model.containsKey('recordCollectionEntity')
    }
}
