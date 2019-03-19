package org.modelcatalogue.stringtransformer

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class CamelCaseSplitTransformerSpec extends Specification {

    @Subject
    @Shared
    CamelCaseSplitTransformer transformer = new CamelCaseSplitTransformer()

    @Unroll("#input => (Split CamelCase) => #result")
    def "verify camel case split"(String input, String result) {
        expect:
        transformer.transform(input) == result

        where:
        input             || result
        "lowercase"       || 'lowercase'
        "Class"           || 'Class'
        "MyClass"         || 'My Class'
        "myClassFoo"      || 'my Class Foo'
        "HTML"            || 'HTML'
        "PDFLoader"       || 'PDF Loader'
        "AString"         || 'A String'
        "SimpleXMLParser" || 'Simple XML Parser'
        "GL11Version"     || 'GL 11 Version'
        "99Bottles"       || '99 Bottles'
        "May5"            ||  'May 5'
        "BFG9000"         || 'BFG 9000'
    }
}
