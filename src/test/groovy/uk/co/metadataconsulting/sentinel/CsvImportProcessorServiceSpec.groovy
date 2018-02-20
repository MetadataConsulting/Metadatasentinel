package uk.co.metadataconsulting.sentinel

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class CsvImportProcessorServiceSpec extends Specification implements ServiceUnitTest<CsvImportProcessorService> {

    def "process DIDS_XMLExample_01.csvloinc returns every line"() {
        when:
        File f = new File('src/test/resources/DIDS_XMLExample_50000.csv')

        then:
        f.exists()

        when:
        InputStream inputStream = f.newInputStream()
        List<List<String>> result = []
        Closure cls = { List<String> l -> }
        service.processInputStream(inputStream, 1000, cls) { List<List<String>> loincList ->
            result.addAll(loincList)
        }

        then:
        result
        result.size() == 44002
    }
}
