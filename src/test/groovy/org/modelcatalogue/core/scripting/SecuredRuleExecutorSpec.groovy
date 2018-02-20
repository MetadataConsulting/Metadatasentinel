package org.modelcatalogue.core.scripting

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class SecuredRuleExecutorSpec extends Specification {

    @Unroll
    def "Evaluation expression throws no error: #exp"(String exp) {
        given:
        SecuredRuleExecutor executor = [x: new Object()]

        when:
        executor.execute(exp)

        then:
        noExceptionThrown()

        where:
        exp << [
                "x",
                "'text'",
                "true",
                "x.unitOfMeasure",
                "y = x",
                "int y = x"
        ]
    }

    @Unroll
    def "Validates valid expression: #exp"(String exp) {
        given:
        SecuredRuleExecutor executor = [x: new Object()]

        expect:
        executor.validate(exp)

        where:
        exp << [
            "x",
            "'text'",
            "true",
            "x.unitOfMeasure",
            "y = x",
            "int y = x",
        ]
    }

    @Unroll
    def "Validates invalid expression: #exp"(String exp) {
        given:
        SecuredRuleExecutor executor = [x: new Object()]

        expect:
        !executor.validate(exp)

        where:
        exp << [
            "System.exit(0)",
            "throw new RuntimeException()",
            "x.delete()",
            "package org.modelcatalogue.core.util",
            "import org.modelcatalogue.core.util.SecuredRuleExecutor",
            "y",
            "x.unitOfMeasure = null",
        ]
    }

    def "cleaned up message"() {
        given:
        String full = '''startup failed:
General error during canonicalization: Expression [VariableExpression] is not allowed: y

java.lang.SecurityException: Expression [VariableExpression] is not allowed: y
\tat org.codehaus.groovy.control.customizers.SecureASTCustomizer$SecuringCodeVisitor.assertExpressionAuthorized(SecureASTCustomizer.java:690)'''

        String brief = SecuredRuleExecutor.cleanUpMessage(full)

        expect:
        brief == 'variable is not allowed: y'
    }
}
