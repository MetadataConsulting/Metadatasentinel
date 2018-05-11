package uk.co.metadataconsulting.sentinel

import spock.lang.Specification

class XlxReaderSpec extends Specification {

    List<List<String>> expectedLines =  [
            ['NHSNumber', 'Empty', 'NHSNumberStatus','PersonBirthDate','PersonGenderCode','postalCode','GPCodeRegistration','PatientSourceSetting','ReferrerCode','ReferringOrgCode','DiagnosticTestReqDate','DiagnosticTestReqRecDate','ImagingCodeNICIP','ImagingCodeSNOMEDCT','DiagnosticTestDate','ImagingSiteCode','RadiologicalAccessionNumber','Color'],
            ['1234567890', '', '01','1987-01-40','1','LS1 4HY','Y44680','01','C2918341','RR807','1950-09-09','9/10/11','XMAMB','71651007','10/9/11','RR807','RW6A06729288','red'],
            ['1234567890', '', '01','1987-01-11','1','LS1 4HY','Y44680','01','C2918341','RR807','2011-09-09','9/10/11','UGRAF','334531000000104','10/10/86','RR807','RW6A06647291','yellow'],
    ]

    def "reading an excel file does not throw any exception"() {

        when:
        File f = new File('src/test/resources/DIDS_XMLExample_01.xlsx')

        then:
        f.exists()

        when:
        List<List<String>> lines = []
        Closure cls = { List<String> values -> lines << values }
        ExcelReader.read(f.newInputStream(), 0, true, null, cls)

        then:
        noExceptionThrown()
        lines.size() == 2
        lines[0].size() == expectedLines[1].size()
        for (int i = 0; i < lines[0].size(); i++) {
            assert lines[0].get(i) == expectedLines[1].get(i)
        }
        lines[1].size() == expectedLines[2].size()
        for (int i = 0; i < lines[1].size(); i++) {
            assert lines[1].get(i) == expectedLines[2].get(i)
        }
    }

}
