package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import org.modelcatalogue.core.scripting.ValueValidator
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules

@CompileStatic
class ValidatorService {
    ValidateRecordPortionService validateRecordPortionService

    ValidationResult validate(ValidationRules validationRules, String value, RecordGormUrlsAndValues recordGormUrlsAndValues) {
        List<String> gormUrls = recordGormUrlsAndValues.gormUrls
        List<String> values = recordGormUrlsAndValues.values
        String reason = validateRecordPortionService.failureReason(validationRules, gormUrls, values)
        String name = validationRules?.name
        Integer numberOfRulesValidatedAgainst = validationRules?.rules?.size() ?: 0

        if ( validationRules?.validating ) {
            if ( !ValueValidator.validateRule(validationRules.validating, value) ) {
                reason = reason ?: validationRules.validating.toString()
            }
            numberOfRulesValidatedAgainst++
        }
        ValidationStatus status = ValidationStatus.NOT_VALIDATED
        if ( numberOfRulesValidatedAgainst ) {
            status = reason ? ValidationStatus.INVALID : ValidationStatus.VALID
        }
        new ValidationResult(numberOfRulesValidatedAgainst: numberOfRulesValidatedAgainst,
                name: name,
                reason: reason,
                status: status
        )
    }
}