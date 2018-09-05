package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.monitor.modelcatalogue.ValidationRules

@CompileStatic
/**
 * Contains methods for:
 * saving matrices of values to a RecordCollection
 * saving lists of values as a Record,
 * creating RecordPortions from values.
 */
class ImportService {

    RuleFetcherService ruleFetcherService
    RecordGormService recordGormService
    ValidatorService validatorService

    /**
     * Saves a matrix of values as Records to a RecordCollection. Updates the RecordCollection– i.e. doesn't delete its old Records.
     * @param recordCollection
     * @param valuesList
     * @param metadata
     */
    void saveMatrixOfValuesToRecordCollection(RecordCollectionGormEntity recordCollection, List<List<String>> valuesList, MappingMetadata metadata) {
        for ( List<String> values : valuesList ) {
            saveListOfValuesAsRecord(recordCollection, values, metadata)
        }
    }

    /**
     * Save a list of values as a Record to a given RecordCollection.
     * @param recordCollection
     * @param values
     * @param metadata
     */
    void saveListOfValuesAsRecord(RecordCollectionGormEntity recordCollection, List<String> values, MappingMetadata metadata) {
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
        return recordPortionList
    }

    String headerAtIndex(MappingMetadata metadata, int index) {
        if ( !metadata ) {
            return null
        }
        if ( !metadata.headersList ) {
            return null
        }
        if ( metadata.headersList.size() <= index) {
            return null
        }
        metadata.headersList[index]
    }

    /**
     * Simultaneously create a record portion from a value and, if a gormUrl is provided, validate it against the gormUrl.
     * @param gormUrl
     * @param value
     * @param header
     * @param values
     * @param metadata
     * @return
     */
    RecordPortion recordPortionFromValue(String gormUrl,
                                         String value,
                                         String header,
                                         List<String> values,
                                         MappingMetadata metadata) {

        ValidationResult validationResult = new ValidationResult()
        if ( gormUrl ) {
            ValidationRules validationRules = metadata.gormUrlsRules[gormUrl] // get validatonRules from MappingMetadata
            validationResult = validatorService.validate(validationRules, // Validate the value against the rules
                    value,
                    new RecordGormUrlsAndValues(gormUrls: metadata.gormUrls, values: values))
        }

        return new RecordPortion(header: header,
                name: validationResult.name,
                value: value,
                status: validationResult.status,
                reason: validationResult.reason,
                numberOfRulesValidatedAgainst: validationResult.numberOfRulesValidatedAgainst)
    }
}