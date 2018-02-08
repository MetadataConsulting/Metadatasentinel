package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class DlrValidatorService implements RuleValidator {

    @Override
    String validate(String name, String rule, Map<String, String> identifierToValue) {
        new DlrValidator().validate(identifierToValue, rule)
    }
}


