package uk.co.metadataconsulting.sentinel

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

import static javax.servlet.http.HttpServletResponse.SC_OK

class RecordPortionControllerSpec extends Specification implements ControllerUnitTest<RecordPortionController> {

    def "RecordPortionController.show model contains recordPortionList"() {
        given:
        controller.recordPortionGormService = Mock(RecordPortionGormService)

        when:
        params.recordId = 1
        request.method = 'GET'
        Map model = controller.show()

        then:
        response.status == SC_OK
        model
        model.containsKey('recordPortionList')
    }

    def "RecordPortionController.show model contains recordPortionTotal"() {
        given:
        controller.recordPortionGormService = Mock(RecordPortionGormService)

        when:
        params.recordId = 1
        request.method = 'GET'
        Map model = controller.show()

        then:
        response.status == SC_OK
        model
        model.containsKey('recordPortionTotal')
    }
}
