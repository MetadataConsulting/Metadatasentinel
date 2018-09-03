package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.monitor.modelcatalogue.ValidationRules

@CompileStatic
class ImportService {

    RuleFetcherService ruleFetcherService
    RecordGormService recordGormService
    ValidatorService validatorService

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
            String gormUrl = (metadata.gormUrls && metadata.gormUrls.size() > i) ? metadata.gormUrls[i] : null
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

    RecordPortion recordPortionFromValue(String gormUrl,
                                         String value,
                                         String header,
                                         List<String> values,
                                         MappingMetadata metadata) {
        ValidationResult validationResult = new ValidationResult()
        if ( gormUrl ) {
            ValidationRules validationRules = metadata.gormUrlsRules[gormUrl]
            validationResult = validatorService.validate(validationRules,
                    value,
                    new RecordGormUrlsAndValues(gormUrls: metadata.gormUrls, values: values))
        }
        new RecordPortion(header: header,
                name: validationResult.name,
                value: value,
                status: validationResult.status,
                reason: validationResult.reason,
                numberOfRulesValidatedAgainst: validationResult.numberOfRulesValidatedAgainst)
    }
}