package uk.co.metadataconsulting.sentinel

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

import static javax.servlet.http.HttpServletResponse.SC_OK

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
        model.containsKey('recordCollectionListTotal')
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
}
