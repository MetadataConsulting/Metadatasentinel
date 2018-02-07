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


    def "RecordController.index model contains recordCollectionList"() {
        given:
        controller.recordGormService = Mock(RecordGormService)

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
        controller.recordGormService = Mock(RecordGormService)

        when:
        params.recordCollectionId = 1
        request.method = 'GET'
        Map model = controller.index()

        then:
        response.status == SC_OK
        model
        model.containsKey('paginationQuery')
    }
}
