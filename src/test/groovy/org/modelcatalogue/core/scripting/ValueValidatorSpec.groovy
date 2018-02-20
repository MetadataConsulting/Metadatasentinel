package org.modelcatalogue.core.scripting

import spock.lang.Specification
import spock.lang.Unroll

class ValueValidatorSpec extends Specification {

    @Unroll
    def "#rule for #value #description"(String rule, String value, boolean expected, String description) {
        expect:
        expected == ValueValidator.validateRule(new ValidatingImpl(explicitRule: rule), value)

        where:
        rule                                | value | expected
        "x == null || x in ['red', 'blue']" | 're'  | false
        "x == null || x in ['red', 'blue']" | 'red' | true
        description = expected ? 'is valid' : 'is not valid'
    }
}
