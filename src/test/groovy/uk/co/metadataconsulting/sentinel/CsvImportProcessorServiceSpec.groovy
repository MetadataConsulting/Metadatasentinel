package uk.co.metadataconsulting.sentinel

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class CsvImportProcessorServiceSpec extends Specification implements ServiceUnitTest<CsvImportProcessorService> {

    def "process DIDS_XMLExample_01.csvloinc returns every line"() {
        when:
        File f = new File('src/test/resources/DIDS_XMLExample_01.csv')

        then:
        f.exists()

        when:
        InputStream inputStream = f.newInputStream()
        List<List<String>> result = []
        service.processInputStream(inputStream, 100) {  List<List<String>> loincList ->
            result.addAll(loincList)
        }

        then:
        result
        result.size() == 2
    }
}
