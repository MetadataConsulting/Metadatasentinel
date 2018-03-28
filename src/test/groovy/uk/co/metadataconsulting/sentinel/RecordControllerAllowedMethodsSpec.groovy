package uk.co.metadataconsulting.sentinel

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification
import spock.lang.Unroll

import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED
import static javax.servlet.http.HttpServletResponse.SC_OK
import static javax.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY

class RecordControllerAllowedMethodsSpec extends Specification implements ControllerUnitTest<RecordController> {

    @Unroll
    def "test RecordController.index does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.index()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'POST', 'PUT']
    }

    def "test RecordController.index accepts GET requests"() {
        when:
        request.method = 'GET'
        controller.index()

        then:
        response.status == SC_OK
    }

    @Unroll
    def "test RecordController.show does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.show()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'POST', 'PUT']
    }

    def "test RecordController.show accepts GET requests"() {
        given:
        controller.recordPortionGormService = Mock(RecordPortionGormService)
        controller.recordCollectionMappingGormService = Mock(RecordCollectionMappingGormService)

        when:
        request.method = 'GET'
        controller.show()

        then:
        response.status == SC_OK
    }

    @Unroll
    def "test RecordController.validate does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.validate()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'GET', 'PUT']
    }

    def "test RecordController.validate accepts POST requests"() {
        given:
        controller.recordService = Mock(RecordService)
        controller.recordCollectionMappingGormService = Mock(RecordCollectionMappingGormService)
        controller.ruleFetcherService = Mock(RuleFetcherService)

        when:
        params.recordId = 1
        params.recordCollectionId = 5
        request.method = 'POST'
        controller.validate()

        then:
        response.status == SC_MOVED_TEMPORARILY
    }
}
