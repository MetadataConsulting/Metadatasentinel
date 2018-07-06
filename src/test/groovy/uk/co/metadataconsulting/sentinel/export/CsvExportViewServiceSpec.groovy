package uk.co.metadataconsulting.sentinel.export

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class CsvExportViewServiceSpec extends Specification implements ServiceUnitTest<CsvExportService>, RecordCollectionExportViewFixture {

    def "verifies an CSV file is generated with four tabs"() {
        given:
        ExportFormat format = ExportFormat.CSV
        File f = new File("build/${format.toString()}.csv")
        OutputStream os = f.newOutputStream()
        RecordCollectionExportView view = recordCollectionExportView()

        when:
        service.export(os, view)

        then:
        noExceptionThrown()

        when: 'if you check the generated CSV File'
        Map<String, Integer> ocurrences = ['00000000': 0, '(1234567890)': 0]
        for(String line : f.readLines()) {
            for ( String k : ocurrences.keySet()) {
                if(line.contains(k)) {
                    ocurrences[k] = (ocurrences[k] + 1)
                }
            }
        }

        then: 'there are two lines with value 00000000'
        ocurrences['00000000'] == 2

        and: 'there is one line wrapped in parenthesis with value 1234567890'
        ocurrences['(1234567890)'] == 1

        cleanup:
        os.close()
        f.delete()
    }
}
