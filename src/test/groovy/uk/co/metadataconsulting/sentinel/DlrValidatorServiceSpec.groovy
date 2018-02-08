package uk.co.metadataconsulting.sentinel

import grails.testing.services.ServiceUnitTest
import metadata.Validation
import spock.lang.Specification
import spock.lang.Unroll

class DlrValidatorServiceSpec extends Specification implements ServiceUnitTest<DlrValidatorService> {

    @Unroll
    def "#dateOfBirth #description yyyy-mm-dd"(String dateOfBirth, String expected, String description) {
        given:
        String rule = '''
package metadata;

global java.util.List output;
global java.lang.String dateOfBirth;

rule "match yyyy-MM-dd"
when
    eval(!Validation.matchesDatePattern(dateOfBirth, "yyyy-MM-dd"))
then
    output.add( "date does not match yyyy-MM-dd" );
end
'''
        expect:
        expected == service.validate('date as yyyy-MM-dd', rule, [dateOfBirth: dateOfBirth] )

        where:
        dateOfBirth  || expected
        '1987-01-40' || 'date does not match yyyy-MM-dd'
        '1987-01-11' || null
        '2011-09-10' || null
        '2011-09-32' || 'date does not match yyyy-MM-dd'
        description = expected ? 'matches format' : 'does not match format'
    }

    @Unroll
    def "Age calculated ( #diagnosticTestDate - #dateOfBirth ) #description"(String diagnosticTestDate, String dateOfBirth, String expected, String description) {
        given:
        String rule = '''
package metadata;

global java.util.List output;
global java.lang.String diagnosticTestDate;
global java.lang.String dateOfBirth;

rule "Difference between diagnosticTestDate and dateOfBirth is larger than 18"
when
    eval(Validation.yearsBetween(diagnosticTestDate, dateOfBirth, 'yyyy-MM-dd') < 18)
then
    output.add("Difference between diagnosticTestDate and dateOfBirth is larger than 18");
end
'''
        expect:
        expected == service.validate('Difference between diagnosticTestDate and dateOfBirth is larger than 18', rule, [diagnosticTestDate: diagnosticTestDate, dateOfBirth: dateOfBirth] )

        where:
        diagnosticTestDate | dateOfBirth   || expected
        '2011-10-09'       | '1987-01-11'  || null
        '2011-10-09'       | '1999-01-11'  || 'Difference between diagnosticTestDate and dateOfBirth is larger than 18'
        description = expected ? 'is an adult' : 'is a minor'
    }
}
