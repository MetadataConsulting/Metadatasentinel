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
        File f = new File('src/test/resources/DIDS_XMLExample_20.csv')

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
        String mapping = 'gorm://org.modelcatalogue.core.DataElement:53,gorm://org.modelcatalogue.core.DataElement:57,gorm://org.modelcatalogue.core.DataElement:63,gorm://org.modelcatalogue.core.DataElement:71,,gorm://org.modelcatalogue.core.DataElement:44,,gorm://org.modelcatalogue.core.DataElement:80,gorm://org.modelcatalogue.core.DataElement:93,gorm://org.modelcatalogue.core.DataElement:27,gorm://org.modelcatalogue.core.DataElement:33,gorm://org.modelcatalogue.core.DataElement:49,gorm://org.modelcatalogue.core.DataElement:51,gorm://org.modelcatalogue.core.DataElement:16,gorm://org.modelcatalogue.core.DataElement:103,gorm://org.modelcatalogue.core.DataElement:77'
        List<String> mappingList = mapping.split(',')  as List<String>
        csvImportService.save(mappingList, f.newInputStream(), 10)

        then:
        recordCollectionGormService.count() == old(recordCollectionGormService.count()) + 1
        conditions.eventually {
            assert recordGormService.count() == old(recordGormService.count()) + expectedNumberOfRows
            assert recordPortionGormService.count() == old(recordPortionGormService.count()) + (expectedNumberOfRows * mappingList.size())
        }
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