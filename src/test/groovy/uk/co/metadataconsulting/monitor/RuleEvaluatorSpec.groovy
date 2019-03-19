package uk.co.metadataconsulting.monitor

import spock.lang.Specification
import spock.lang.Unroll

class RuleEvaluatorSpec extends Specification {

    @Unroll
    def "#dateOfBirth #description yyyy-mm-dd"(String dateOfBirth, boolean expected, String description) {
        expect:
        expected == Eval.x(dateOfBirth, 'x ==~ /([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))/')

        where:
        dateOfBirth  || expected
        '1987-01-11' || true
        '2011-09-10' || true
        '2011-09-32' || false
        description = expected ? 'matches format' : 'does not match format'
    }

    @Unroll
    def "#diagnosticTestDate #dateOfBirth #description"(String diagnosticTestDate, String dateOfBirth, boolean expected, String description) {
        expect:
        expected == Eval.xy(diagnosticTestDate, dateOfBirth, '((new java.text.SimpleDateFormat(\'yyyy-MM-dd\').parse(x) - new java.text.SimpleDateFormat(\'yyyy-MM-dd\').parse(y))/365) > 18')

        where:
        diagnosticTestDate | dateOfBirth   || expected
        '2011-10-09'       | '1987-01-11'  || true
        '2011-10-09'       | '1999-01-11'  || false
        description = expected ? 'is an adult' : 'is a minor'


    }
}
