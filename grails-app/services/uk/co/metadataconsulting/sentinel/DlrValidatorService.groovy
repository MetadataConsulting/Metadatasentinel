package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class DlrValidatorService implements RuleValidator {

    @Override
    boolean validate(String[] input, String rule) {
        RecordValidation validationResult = new RecordValidation(input: input)
        new DlrValidator().validate(validationResult, rule)
        validationResult.output
    }
}


