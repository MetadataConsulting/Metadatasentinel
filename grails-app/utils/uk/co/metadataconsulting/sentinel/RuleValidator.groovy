package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
interface RuleValidator {
    boolean validate(String[] input, String rule)
}
