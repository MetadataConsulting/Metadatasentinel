package uk.co.metadataconsulting.sentinel

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

import static javax.servlet.http.HttpServletResponse.SC_OK
import static javax.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY

class RecordCollectionControllerSpec extends Specification implements ControllerUnitTest<RecordCollectionController> {

    void "invoking uploadCsv with invalid parameters renders importCsv again"() {
        when:
        request.method = 'POST'
        controller.uploadCsv()

        then:
        response.status == 200
        flash.error != null
        view == '/recordCollection/importCsv'
    }

    def "RecordCollectionController.index model contains recordCollectionList"() {
        given:
        controller.recordCollectionGormService = Mock(RecordCollectionGormService)

        when:
        request.method = 'GET'
        Map model = controller.index()

        then:
        response.status == SC_OK
        model.containsKey('recordCollectionList')
    }

    def "RecordCollectionController.index model contains total"() {
        given:
        controller.recordCollectionGormService = Mock(RecordCollectionGormService)

        when:
        request.method = 'GET'
        Map model = controller.index()

        then:
        response.status == SC_OK
        model.containsKey('recordCollectionTotal')
    }

    def "RecordCollectionController.index model contains paginationQuery"() {
        given:
        controller.recordCollectionGormService = Mock(RecordCollectionGormService)

        when:
        request.method = 'GET'
        Map model = controller.index()

        then:
        response.status == SC_OK
        model.containsKey('paginationQuery')
    }

    def "test RecordCollectionController.headersMapping model contains recordPortionMappingList"() {
        given:
        controller.recordCollectionMappingGormService = Mock(RecordCollectionMappingGormService)
        controller.ruleFetcherService = Mock(RuleFetcherService)

        when:
        request.method = 'GET'
        params.recordCollectionId = 1
        Map model = controller.headersMapping()

        then:
        response.status == SC_OK
        model.containsKey('recordPortionMappingList')
    }

    def "test RecordCollectionController.headersMapping model contains recordCollectionId"() {
        given:
        controller.recordCollectionMappingGormService = Mock(RecordCollectionMappingGormService)
        controller.ruleFetcherService = Mock(RuleFetcherService)

        when:
        request.method = 'GET'
        params.recordCollectionId = 1
        Map model = controller.headersMapping()

        then:
        response.status == SC_OK
        model.containsKey('recordCollectionId')
    }

    def "RecordCollectionController.delete model triggers recordCollectionGormService.delete invocation"() {
        given:
        controller.recordCollectionGormService = Mock(RecordCollectionGormService)

        when:
        params.recordCollectionId = 1
        request.method = 'POST'
        controller.delete()

        then:
        response.status == SC_MOVED_TEMPORARILY
        1 * controller.recordCollectionGormService.delete(1)
    }
}
