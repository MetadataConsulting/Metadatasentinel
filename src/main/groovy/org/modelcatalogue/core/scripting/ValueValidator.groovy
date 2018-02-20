package org.modelcatalogue.core.scripting

import groovy.transform.CompileStatic


/**
 * Helper class to validate Validating objects.
 */
@CompileStatic
class ValueValidator {

    static boolean validateRule(Validating ruleSource, Object x) {
        String explicitRule = ruleSource.explicitRule
        if (explicitRule && !evaluateRule(explicitRule, x)) {
            return false
        }

        List<Validating> validatingList = ruleSource.bases as List<Validating>
        if ( validatingList ) {
            for (Validating validating in validatingList) {
                if (!validateRule(validating, x)) {
                    return false
                }
            }
        }

        if (ruleSource.implicitRule) {
            return evaluateRule(ruleSource.implicitRule, x)
        }

        return true
    }

    static boolean evaluateRule(String rule, Object x) {
        def result = new SecuredRuleExecutor(DataTypeRuleScript, new Binding(x: x)).execute(rule)
        if (result instanceof Exception) {
            return false
        }
        else if (result != null && (!(result instanceof Boolean) || result.is(false))) {
            return result
        }
        return true
    }

}
