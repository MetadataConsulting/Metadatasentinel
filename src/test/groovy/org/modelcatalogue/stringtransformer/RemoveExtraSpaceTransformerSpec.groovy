package org.modelcatalogue.stringtransformer

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class RemoveExtraSpaceTransformerSpec extends Specification {

    @Subject
    @Shared
    RemoveExtraSpaceTransformer transformer = new RemoveExtraSpaceTransformer()

    @Unroll("#input => remove extra space => #result")
    def "verify remove extraspace"(String input, String result) {
        expect:
        transformer.transform(input) == result

        where:
        input            || result
        "Hello world"    || "Hello world"
        " Hello  world " || "Hello world"
    }
}
