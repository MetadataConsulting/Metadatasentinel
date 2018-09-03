package uk.co.metadataconsulting.monitor.security

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification
import spock.lang.Unroll

import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED
import static javax.servlet.http.HttpServletResponse.SC_OK

class RegisterControllerAllowedMethodsSpec extends Specification implements ControllerUnitTest<RegisterController> {
    @Unroll
    def "test RegisterController.index does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.index()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'POST', 'PUT']
    }

    def "test RegisterController.index accepts GET requests"() {
        given:

        when:
        request.method = 'GET'
        controller.index()


        then:
        response.status == SC_OK
    }
}
