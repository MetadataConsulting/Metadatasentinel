package uk.co.metadataconsulting.sentinel

import grails.testing.mixin.integration.Integration
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@Integration
class CsvImportServiceIntegrationSpec extends Specification {

    CsvImportService csvImportService
    RecordCollectionGormService recordCollectionGormService
    RecordGormService recordGormService
    RecordPortionGormService recordPortionGormService
    def conditions = new PollingConditions(timeout: 30)

    def "save processes an CSV file and imports a record collection"() {
        when:
        String filename = 'src/test/resources/DIDS_XMLExample_20.csv'
        File f = new File(filename)

        then:
        f.exists()

        when:
        int expectedNumberOfRows = numberOfLines(filename)
        boolean skipFirstLine = true
        if ( skipFirstLine ) {
            expectedNumberOfRows -= 1
        }

        then:
        expectedNumberOfRows

        when:
        csvImportService.save(f.newInputStream(), 50)

        then:
        recordCollectionGormService.count() == old(recordCollectionGormService.count()) + 1

        conditions.eventually {
            assert recordGormService.count() == old(recordGormService.count()) + expectedNumberOfRows
            assert recordPortionGormService.count() == old(recordPortionGormService.count()) + (expectedNumberOfRows * numberOfItemsPerLine(filename))
        }
    }

    int numberOfItemsPerLine(String filename) {
        BufferedReader reader = new BufferedReader(new FileReader(filename))
        String line = reader.readLine()
        int result = line.split(',').size()
        reader.close()
        result
    }

    int numberOfLines(String filename) {
        BufferedReader reader = new BufferedReader(new FileReader(filename))
        int lines = 0
        while (reader.readLine() != null) {
            lines++
        }
        reader.close()
        lines
    }
}