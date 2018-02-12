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
    def "#input #description"(String input, boolean expected, String description) {
        expect:
        expected == !Validation.matchesRegex(input, '1234567890|0123456789|00000000001|00000000002|00000000003|00000000004|00000000005|00000000006|00000000007|00000000008|00000000009')

        where:
        input         || expected
        '1234000000'  || true
        '1234567890'  || false
        '0123456789'  || false
        '00000000001' || false
        '00000000002' || false
        '00000000003' || false
        '00000000004' || false
        '00000000005' || false
        '00000000006' || false
        '00000000007' || false
        '00000000008' || false
        '00000000009' || false
        description = expected ? 'is valid' : 'is not valid'
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