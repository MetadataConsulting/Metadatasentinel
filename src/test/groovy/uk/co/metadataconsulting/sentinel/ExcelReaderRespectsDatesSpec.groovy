package uk.co.metadataconsulting.sentinel

import spock.lang.Specification

class ExcelReaderRespectsDatesSpec extends Specification {

    def "reading an excel file does not throw any exception"() {

        expect:
        def numerics = [
                '1',
                '2',
                '1',
                '1',
                '2',
                '1',
                '1',
                '2',
                '1',
                '1',
                '2',
                '3',
                '4',
                '1',
                '1',
                '1',
                '2',
                '2',
                '',
                '2',
                '1',
                '1',
                '1',
                '1',
        ]
        def expected = [
                '2011-02-12',
                '2011-03-13',
                '01/02/2013',
                '2011-02-12',
                '2011-03-13',
                '01/02/2013',
                '2011-02-12',
                '2011-03-13',
                '01/02/2013',
                '2011-02-12',
                '2011-03-13',
                '01/02/2013',
                '2011-02-12',
                '2011-03-13',
                '01/02/2013',
                '2011-02-12',
                '2011-03-13',
                '01/02/2013',
                '2011-02-12',
                '2011-03-13',
                '01/02/2013',
                '2011-02-12',
                '2011-03-13',
                '01/02/2013']

        when:
        File f = new File('src/test/resources/CEGX_Example2.xlsx')

        then:
        f.exists()

        when:
        List<List<String>> lines = []
        Closure cls = { List<String> values -> lines << values }
        ExcelReader.read(f.newInputStream(), 0, true, null, cls)

        then:
        lines.size() == 24
        lines.collect { it[3] } == expected
        lines.collect { it[4] } == numerics
    }

}
