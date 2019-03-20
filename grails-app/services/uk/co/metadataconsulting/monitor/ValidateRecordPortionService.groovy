package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.monitor.modelcatalogue.ValidationRule
import uk.co.metadataconsulting.monitor.modelcatalogue.ValidationRules

@CompileStatic
class ValidateRecordPortionService {

    DlrValidatorService dlrValidatorService
    ValidatorService validatorService

    /**
     * Uses the GORM URL of a RecordPortion, found by the recordPortionMappingList, to find the relevant set of ValidationRules
     * from the validationRulesMap.
     * @param recordPortion
     * @param recordPortionMappingList
     * @param validationRulesMap
     * @return
     */
    ValidationRules validationRulesByRecordPortion(RecordPortionGormEntity recordPortion, List<RecordPortionMapping> recordPortionMappingList, Map<String, ValidationRules> validationRulesMap) {
        String recordPortionGormUrl = gormUrlByRecordPortionGormEntity(recordPortionMappingList, recordPortion)
        validationRulesMap.get(recordPortionGormUrl)
    }

    /**
     * Method that validates a recordPortion against
     * A. associated MDX CatalogueElement (found by recordPortionMappingList)
     * and B. ValidationRules associated with that MDX CatalogueElement (validationRulesMap)
     *
     * Actually validates in context of other related recordPortion-values-cells in the same record.
     *
     * @param recordPortion
     * @param recordPortionMappingList
     * @param validationRulesMap
     * @return
     */
    ValidationResult failureReason(RecordPortionGormEntity recordPortion, List<RecordPortionMapping> recordPortionMappingList, Map<String, ValidationRules> validationRulesMap) {
        ValidationRules validationRules = validationRulesByRecordPortion(recordPortion, recordPortionMappingList, validationRulesMap)
        RecordGormUrlsAndValues recordGormUrlsAndValues = recordGormUrlsAndValuesByRecordPortion(recordPortionMappingList, recordPortion)
        validatorService.validate(validationRules, recordPortion.value, recordGormUrlsAndValues)
    }

    /**
     * Essentially recovers the Record from the RecordPortion...
     * @param recordPortionMappingList
     * @param recordPortion
     * @return
     */
    RecordGormUrlsAndValues recordGormUrlsAndValuesByRecordPortion(List<RecordPortionMapping> recordPortionMappingList, RecordPortionGormEntity recordPortion) {
        List<String> gormUrls = []
        List<String> values = []
        recordPortion.record.portions.each { RecordPortionGormEntity recordPortionGormEntity ->
            values << recordPortionGormEntity.value
            gormUrls << gormUrlByRecordPortionGormEntity(recordPortionMappingList, recordPortionGormEntity)
        }
        new RecordGormUrlsAndValues(values: values, gormUrls: gormUrls)
    }

    String gormUrlByRecordPortionGormEntity(List<RecordPortionMapping> recordPortionMappingList, RecordPortionGormEntity recordPortionGormEntity) {
        recordPortionMappingList.find { RecordPortionMapping recordPortionMapping ->
            recordPortionMapping.header == recordPortionGormEntity.header
        }?.gormUrl
    }

    /**
     * Method that executes the rules of "validationRules" in a Drools Rule Engine.
     * The list of gormUrls and list of values should be of the same length;
     * "values[i]" is the value in the cell in the column/header which is associated with the CatalogueElement identified by "gormUrls[i]".
     * @param validationRules
     * @param gormUrls
     * @param values
     * @return
     */
    String executeValidationRulesWithDrools(ValidationRules validationRules, List<String> gormUrls, List<String> values) {
        String reason
        if ( !validationRules?.rules ) {
            return reason
        }
        for ( ValidationRule validationRule : validationRules.rules ) {

            // Create a mapping from identifiers (Drools global variables) to values to be validated.
            // This makes up part of the "environment" against which the rule is executed
            Map m = [:]
            for ( String identifier : validationRule.identifiersToGormUrls.keySet() ) {
                m[identifier] = valuesOfGormUrl(validationRule.identifiersToGormUrls[identifier], gormUrls, values)
            }

            reason = dlrValidatorService.validate(validationRule.name, validationRule.rule, m)
            if ( reason!=null ) {
                break
            }
        }
        reason
    }

    int indexOfGormUrl(List<String> gormUrls, String gormUrl) {
        for ( int i = 0; i < gormUrls.size(); i++ ) {
            if ( gormUrls[i] == gormUrl ) {
                return i
            }
        }
        return -1
    }

    String valuesOfGormUrl(String gormUrl,  List<String> gormUrls, List<String> values) {
        int index = indexOfGormUrl(gormUrls, gormUrl)
        if ( index != -1 ) {
            return values[index]
        }
        null
    }


}