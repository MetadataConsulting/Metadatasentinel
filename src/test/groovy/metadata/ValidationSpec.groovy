package metadata

import spock.lang.Specification
import spock.lang.Unroll

class ValidationSpec extends Specification {

    @Unroll
    def "#dateOfBirth #description yyyy-mm-dd"(String dateOfBirth, boolean expected, String description) {
        given:
        expected == Validation.matchesDatePattern(dateOfBirth, 'yyyy-mm-dd')

        where:
        dateOfBirth  || expected
        '1987-01-40' || false
        '1987-01-11' || true
        '2011-09-10' || true
        '2011-09-32' || false
        description = expected ? 'matches format' : 'does not match format'
    }

    @Unroll
    def "between #last and #first there are #years years"(String last, String first, int years) {
        expect:
        years == Validation.yearsBetween(last, first, 'yyyy-MM-dd')

        where:
        last         | first        || years
        '2011-10-09' | '1987-01-11' || 24
        '2011-10-09' | '1999-01-11' || 12
    }
    @Unroll
    def "Blank validation: #input #description"(String input, boolean expected, String description) {
        expect:
        expected == Validation.blank(input)

        where:
        input || expected
        '123' || false
        null  || true
        ''    || true
        '  '  || true
        description = expected ? 'is blank' : 'is not blank'
    }
}