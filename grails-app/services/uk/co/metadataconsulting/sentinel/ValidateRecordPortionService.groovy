package uk.co.metadataconsulting.sentinel

import grails.gorm.DetachedCriteria
import groovy.transform.CompileStatic
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRule
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules

@CompileStatic
class ValidateRecordPortionService {

    DlrValidatorService dlrValidatorService
    RuleFetcherService ruleFetcherService
    RecordGormService recordGormService

    String failureReason(RecordPortionGormEntity recordPortion, List<RecordPortionMapping> recordPortionMappingList) {
        String recordPortionGormUrl = gormUrlByRecordPortionGormEntity(recordPortionMappingList, recordPortion)
        ValidationRules validationRules = ruleFetcherService.fetchValidationRules(recordPortionGormUrl)
        List<String> gormUrls = []
        List<String> values = []
        recordPortion.record.portions.each { RecordPortionGormEntity recordPortionGormEntity ->
            values << recordPortionGormEntity.value
            gormUrls << gormUrlByRecordPortionGormEntity(recordPortionMappingList, recordPortionGormEntity)
        }
        failureReason(validationRules, gormUrls, values)
    }

    String gormUrlByRecordPortionGormEntity(List<RecordPortionMapping> recordPortionMappingList, RecordPortionGormEntity recordPortionGormEntity) {
        recordPortionMappingList.find { RecordPortionMapping recordPortionMapping ->
            recordPortionMapping.header == recordPortionGormEntity.header
        }?.gormUrl
    }

    String failureReason(ValidationRules validationRules, List<String> gormUrls, List<String> values) {
        String reason
        if ( !validationRules?.rules ) {
            return reason
        }
        for ( ValidationRule validationRule : validationRules.rules ) {

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