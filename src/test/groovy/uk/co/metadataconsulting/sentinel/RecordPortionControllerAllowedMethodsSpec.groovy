package uk.co.metadataconsulting.sentinel

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification
import spock.lang.Unroll

import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED
import static javax.servlet.http.HttpServletResponse.SC_OK

class RecordPortionControllerAllowedMethodsSpec extends Specification implements ControllerUnitTest<RecordPortionController> {

    @Unroll
    def "test RecordPortionController.show does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.show()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'POST', 'PUT']
    }

    def "test RecordPortionController.show accepts GET requests"() {
        given:
        controller.recordPortionGormService = Mock(RecordPortionGormService)
        when:
        request.method = 'GET'
        controller.show()

        then:
        response.status == SC_OK
    }
}
