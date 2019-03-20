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
        String droolsOutput = validateRecordPortionService.executeValidationRulesWithDrools(validationRules, gormUrls, values)

        String name = validationRules?.name
        Integer numberOfRulesValidatedAgainst = validationRules?.rules?.size() ?: 0

        String validatingImplOutput = ""
        // Validate against the "ValidatingImpl"
        if ( validationRules?.validating ) {
            if ( !ValueValidator.validateRule(validationRules.validating, value) ) {
                validatingImplOutput = validationRules.validating.toString()
            }
            numberOfRulesValidatedAgainst++
        }
        String reason = ""
        if (droolsOutput) {
            if (validatingImplOutput) {
                reason = "${droolsOutput}, Groovy Rule: [${validatingImplOutput}]"
            }
            else {
                reason = droolsOutput
            }

        }
        else if (validatingImplOutput) {
            reason = validatingImplOutput
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