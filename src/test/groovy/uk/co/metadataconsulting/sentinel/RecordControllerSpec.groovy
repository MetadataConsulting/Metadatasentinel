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

    def "RecordController.index model contains recordCollectionId"() {
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
        model.containsKey('recordCollectionId')
    }

    def "RecordController.index model contains invalidRecordTotal"() {
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
        model.containsKey('invalidRecordTotal')
    }

    def "RecordController.index model contains allRecordTotal"() {
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
        model.containsKey('allRecordTotal')
    }

    def "RecordController.index model contains recordList"() {
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
        model.containsKey('recordList')
    }

    def "RecordController.index model contains paginationQuery"() {
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
        model.containsKey('paginationQuery')
    }

    def "RecordController.recordTotal model contains paginationQuery"() {
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

    def "RecordController.show model contains recordPortionList"() {
        given:
        controller.recordPortionGormService = Mock(RecordPortionGormService)
        controller.recordCollectionMappingGormService = Mock(RecordCollectionMappingGormService)

        when:
        params.recordId = 1
        request.method = 'GET'
        Map model = controller.show()

        then:
        response.status == SC_OK
        model
        model.containsKey('recordPortionList')
    }

    def "RecordController.show model contains recordId"() {
        given:
        controller.recordPortionGormService = Mock(RecordPortionGormService)
        controller.recordCollectionMappingGormService = Mock(RecordCollectionMappingGormService)

        when:
        params.recordId = 1
        request.method = 'GET'
        Map model = controller.show()

        then:
        response.status == SC_OK
        model
        model.containsKey('recordId')
    }

    def "RecordController.show model contains recordPortionTotal"() {
        given:
        controller.recordPortionGormService = Mock(RecordPortionGormService)
        controller.recordCollectionMappingGormService = Mock(RecordCollectionMappingGormService)

        when:
        params.recordId = 1
        request.method = 'GET'
        Map model = controller.show()

        then:
        response.status == SC_OK
        model
        model.containsKey('recordPortionTotal')
    }

    def "RecordController.index model contains createdBy"() {
        given:
        controller.recordPortionGormService = Mock(RecordPortionGormService)
        controller.recordCollectionMappingGormService = Mock(RecordCollectionMappingGormService)
        controller.recordService = Stub(RecordService) {
            countByRecordCollectionIdAndCorrectness() >> 1
        }
        controller.recordCollectionGormService = Stub(RecordCollectionGormService) {
            find(_) >> new RecordCollectionGormEntity(createdBy: 'username')
        }

        when:
        params.recordCollectionId = 1
        request.method = 'GET'
        Map model = controller.index()

        then:
        response.status == SC_OK
        model
        model.containsKey('createdBy')
    }

    def "RecordController.index model contains updatedBy"() {
        given:
        controller.recordPortionGormService = Mock(RecordPortionGormService)
        controller.recordCollectionMappingGormService = Mock(RecordCollectionMappingGormService)
        controller.recordService = Stub(RecordService) {
            countByRecordCollectionIdAndCorrectness() >> 1
        }
        controller.recordCollectionGormService = Stub(RecordCollectionGormService) {
            find(_) >> new RecordCollectionGormEntity(updatedBy: 'username')
        }
        when:
        params.recordCollectionId = 1
        request.method = 'GET'
        Map model = controller.index()

        then:
        response.status == SC_OK
        model
        model.containsKey('updatedBy')
    }
}
