package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic
import org.modelcatalogue.core.scripting.ValueValidator
import uk.co.metadataconsulting.monitor.modelcatalogue.ValidationRules

@CompileStatic
class ValidatorService {
    ValidateRecordPortionService validateRecordPortionService

    /**
     * Validates a value against a validatingImpl
     * and also validates the whole record that it comes from (represented by RecordGormUrlsAndValues),
     * against a set of (Drools) validationRules.
     *
     * @param validationRules a set of validation rules, associated with the MDX Catalogue Element, associated with the value to be validated
     * @param value value to be validated
     * @param recordGormUrlsAndValues
     * @return
     */
    ValidationResult validate(ValidationRules validationRules, String value, RecordGormUrlsAndValues recordGormUrlsAndValues) {

        List<String> gormUrls = recordGormUrlsAndValues.gormUrls
        List<String> values = recordGormUrlsAndValues.values

        // Validate against Drools Validation Rules
        String reason = validateRecordPortionService.executeValidationRulesWithDrools(validationRules, gormUrls, values)

        String name = validationRules?.name
        Integer numberOfRulesValidatedAgainst = validationRules?.rules?.size() ?: 0

        // Validate against the "ValidatingImpl"
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