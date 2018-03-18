package uk.co.metadataconsulting.sentinel

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification
import spock.lang.Unroll

import static javax.servlet.http.HttpServletResponse.*

class RecordCollectionMappingControllerAllowedMethodsSpec extends Specification
        implements ControllerUnitTest<RecordCollectionMappingController> {

    @Unroll
    def "test RecordCollectionController.update does not accept #method requests"(String method) {
        when:
        request.method = method
        controller.update()

        then:
        response.status == SC_METHOD_NOT_ALLOWED

        where:
        method << ['PATCH', 'DELETE', 'GET', 'PUT']
    }

    def "test RecordCollectionController.update accepts POST requests"() {
        given:
        controller.recordCollectionMappingGormService = Mock(RecordCollectionMappingGormService)

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.status == SC_FOUND
    }
}
