package uk.co.metadataconsulting.sentinel

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification
import spock.lang.Unroll
import static javax.servlet.http.HttpServletResponse.SC_OK
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED
import static javax.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY

class RecordCollectionControllerAllowedMethodsSpec extends Specification implements ControllerUnitTest<RecordCollectionController> {

    @Unroll
    def "test RecordCollectionController.index does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.index()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'POST', 'PUT']
    }

    def "test RecordCollectionController.index accepts GET requests"() {
        given:
        controller.recordCollectionGormService = Mock(RecordCollectionGormService)

        when:
        request.method = 'GET'
        controller.index()

        then:
        response.status == SC_OK
    }

    @Unroll
    def "test RecordCollectionController.importCsv does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.importCsv()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'POST', 'PUT']
    }

    def "test RecordCollectionController.importCsv accepts GET requests"() {
        when:
        request.method = 'GET'
        controller.importCsv()

        then:
        response.status == SC_OK
    }

    @Unroll
    def "test RecordCollectionController.uploadCsv does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.uploadCsv()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'GET', 'PUT']
    }

    def "test RecordCollectionController.uploadCsv accepts POST requests"() {
        when:
        request.method = 'POST'
        controller.uploadCsv()

        then:
        response.status == SC_OK
    }

    @Unroll
    def "test RecordCollectionController.delete does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.delete()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'GET', 'PUT']
    }

    def "test RecordCollectionController.delete accepts POST requests"() {
        when:
        request.method = 'POST'
        controller.delete()

        then:
        response.status == SC_MOVED_TEMPORARILY
    }


    @Unroll
    def "test RecordCollectionController.headersMapping does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.headersMapping()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'POST', 'PUT']
    }

    def "test RecordCollectionController.headersMapping accepts GET requests"() {
        given:
        controller.recordPortionMappingGormService = Mock(RecordPortionMappingGormService)

        when:
        request.method = 'GET'
        controller.headersMapping()

        then:
        response.status == SC_OK
    }
}
