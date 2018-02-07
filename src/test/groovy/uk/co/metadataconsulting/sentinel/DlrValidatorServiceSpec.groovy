package uk.co.metadataconsulting.sentinel

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification
import spock.lang.Unroll

class DlrValidatorServiceSpec extends Specification implements ServiceUnitTest<DlrValidatorService> {

    @Unroll
    def "#dateOfBirth #description yyyy-mm-dd"(String dateOfBirth, boolean expected, String description) {
        given:
        String rule = '''
package uk.co.metadataconsulting.sentinel;

global uk.co.metadataconsulting.sentinel.RecordValidation record;

rule "date as yyyy-MM-dd"
   when
      eval(java.util.regex.Pattern.compile("([12]\\\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\\\d|3[01]))").matcher(record.getInput()[0]).matches())
   then
      record.setOutput(true);
end    
'''
        expect:
        expected == service.validate([dateOfBirth] as String[], rule)

        where:
        dateOfBirth  || expected
        '1987-01-11' || true
        '2011-09-10' || true
        '2011-09-32' || false
        description = expected ? 'matches format' : 'does not match format'
    }

    @Unroll
    def "#diagnosticTestDate #dateOfBirth #description"(String diagnosticTestDate, String dateOfBirth, boolean expected, String description) {
        given:
        String rule = '''
package uk.co.metadataconsulting.sentinel;

global uk.co.metadataconsulting.sentinel.RecordValidation record;

rule "Age inferred from dates difference should be > 18"
   when
      eval(uk.co.metadataconsulting.sentinel.RecordValidation.getDiffYears(new java.text.SimpleDateFormat('yyyy-MM-dd').parse(record.getInput()[0]), new java.text.SimpleDateFormat('yyyy-MM-dd').parse(record.getInput()[1])) > 18)
   then
      record.setOutput(true);
end    
'''
        expect:
        expected == service.validate([diagnosticTestDate, dateOfBirth] as String[], rule)

        where:
        diagnosticTestDate | dateOfBirth   || expected
        '2011-10-09'       | '1987-01-11'  || true
        '2011-10-09'       | '1999-01-11'  || false
        description = expected ? 'is an adult' : 'is a minor'
    }
}
