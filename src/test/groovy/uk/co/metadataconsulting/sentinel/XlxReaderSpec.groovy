package uk.co.metadataconsulting.sentinel

import spock.lang.Specification

class XlxReaderSpec extends Specification {

    def "reading an excel file does not throw any exception"() {

        when:
        File f = new File('src/test/resources/DIDS_XMLExample_01.xlsx')

        then:
        f.exists()

        when:
        List<List<Object>> lines = []
        Closure cls = { List<String> values -> lines << values }
        ExcelReader.read(f.newInputStream(), 0, true, null, cls)

        then:
        noExceptionThrown()
        lines.size() == 2
        lines.each { List<Object> line ->
            assert line.any { it }
        }

    }
}
