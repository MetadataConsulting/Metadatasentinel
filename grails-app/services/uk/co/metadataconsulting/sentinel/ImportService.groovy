package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import org.modelcatalogue.core.scripting.ValueValidator
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules

@CompileStatic
class ImportService {

    RuleFetcherService ruleFetcherService
    RecordGormService recordGormService
    ValidateRecordPortionService validateRecordPortionService

    MappingMetadata mappingMetadata(List<String> gormUrls) {
        Map<String, ValidationRules> gormUrlsRules = ruleFetcherService.fetchValidationRules(gormUrls)
        new MappingMetadata(gormUrls: gormUrls, gormUrlsRules: gormUrlsRules)
    }

    void saveListOfValues(RecordCollectionGormEntity recordCollection, List<List<String>> valuesList, MappingMetadata metadata) {
        for ( List<String> values : valuesList ) {
            save(recordCollection, values, metadata)
        }
    }

    void save(RecordCollectionGormEntity recordCollection, List<String> values, MappingMetadata metadata) {
        List<RecordPortion> recordPortionList = recordPortionListOfValue(values, metadata)
        recordGormService.save(recordCollection, recordPortionList)
    }

    List<RecordPortion> recordPortionListOfValue(List<String> values, MappingMetadata metadata) {
        List<RecordPortion> recordPortionList = []
        for ( int i = 0; i < values.size(); i++ ) {
            String value = values[i]
            String header = headerAtIndex(metadata, i)
            String gormUrl = metadata.gormUrls[i]
            recordPortionList << recordPortionFromValue(gormUrl, value, header, values, metadata)
        }
        recordPortionList
    }

    String headerAtIndex(MappingMetadata metadata, int index) {
        if ( !metadata ) {
            return null
        }
        if ( !metadata.headerLineList ) {
            return null
        }
        if ( metadata.headerLineList.size() <= index) {
            return null
        }
        metadata.headerLineList[index]
    }

    RecordPortion recordPortionFromValue(String gormUrl, String value, String header, List<String> values, MappingMetadata metadata) {
        ValidationRules validationRules = metadata.gormUrlsRules[gormUrl]

        String reason
        String name
        int numberOfRulesValidatedAgainst = 0

        if ( validationRules ) {
            reason = validateRecordPortionService.failureReason(validationRules, metadata.gormUrls, values)
            name = validationRules.name
            numberOfRulesValidatedAgainst = validationRules.rules?.size() ?: 0

            if ( validationRules.validating ) {
                if ( !ValueValidator.validateRule(validationRules.validating, value) ) {
                    reason = reason ?: validationRules.validating.toString()
                }
                numberOfRulesValidatedAgainst++
            }
        }

        new RecordPortion(header: header,
                name: name,
                url: validationRules?.url,
                gormUrl: gormUrl,
                value: value,
                valid: !(reason as boolean),
                reason: reason,
                numberOfRulesValidatedAgainst: numberOfRulesValidatedAgainst)
    }
}