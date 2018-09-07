package uk.co.metadataconsulting.monitor.validationTask

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification
import uk.co.metadataconsulting.monitor.RuleFetcherService
import uk.co.metadataconsulting.monitor.SaveRecordCollectionService

import static javax.servlet.http.HttpServletResponse.SC_OK

class ValidationTaskControllerSpec extends Specification implements ControllerUnitTest<ValidationTaskController> {
    def "ValidationTaskController.index model contains fields validationTaskList, paginationQuery, validationTaskTotal"() {
        given:
        controller.validationTaskGormService = Mock(ValidationTaskGormService)

        when:
        request.method = 'GET'
        Map model = controller.index()

        then:
        response.status == SC_OK
        model.containsKey(field)

        where:
        field << ['validationTaskList', 'paginationQuery', 'validationTaskTotal']
    }

    def "ValidationTaskController.importCsv model contains field dataModelList, validationTask"() {
        given:
        controller.ruleFetcherService = Mock(RuleFetcherService)

        when:
        request.method = 'GET'
        Map model = controller.importCsv()

        then:
        response.status == SC_OK
        model.containsKey(field)

        where:
        field << ['dataModelList', 'validationTask']
    }

//    validationTaskList: validationTaskList,
//    paginationQuery: paginationQuery,
//    validationTaskTotal: validationTaskListTotal,
}
