package uk.co.metadataconsulting.monitor.export

import uk.co.metadataconsulting.monitor.RecordPortion
import uk.co.metadataconsulting.monitor.ValidationStatus

trait RecordCollectionExportViewFixture {

    RecordCollectionExportView recordCollectionExportView() {
        new RecordCollectionExportView(
                headers: ['NHSNumber', 'NHSNumberStatus', 'PersonBirthDate'],
                rows: [
                        new RecordCollectionExportRowView(recordPortionList: [
                                new RecordPortion(header: 'NHSNumber',
                                        status: ValidationStatus.INVALID,
                                        value: '1234567890',
                                        numberOfRulesValidatedAgainst: 1,
                                        reason: 'Invalid format'
                                ),
                                new RecordPortion(header: 'NHSNumberStatus',
                                        status: ValidationStatus.NOT_VALIDATED,
                                        value: '01'
                                ),
                                new RecordPortion(header: 'PersonBirthDate',
                                        status: ValidationStatus.VALID,
                                        value: '1987-01-40',
                                        numberOfRulesValidatedAgainst: 2,
                                )
                        ]),
                        new RecordCollectionExportRowView(recordPortionList: [
                                new RecordPortion(header: 'NHSNumber',
                                        status: ValidationStatus.NOT_VALIDATED,
                                        value: '00000000',
                                        numberOfRulesValidatedAgainst: 0,
                                ),
                                new RecordPortion(header: 'NHSNumberStatus',
                                        status: ValidationStatus.NOT_VALIDATED,
                                        value: '01'
                                ),
                                new RecordPortion(header: 'PersonBirthDate',
                                        status: ValidationStatus.NOT_VALIDATED,
                                        value: '1982-10-28',
                                        numberOfRulesValidatedAgainst: 0,
                                )
                        ]),
                        new RecordCollectionExportRowView(recordPortionList: [
                                new RecordPortion(header: 'NHSNumber',
                                        status: ValidationStatus.NOT_VALIDATED,
                                        value: '00000000',
                                        numberOfRulesValidatedAgainst: 0,
                                ),
                                new RecordPortion(header: 'NHSNumberStatus',
                                        status: ValidationStatus.NOT_VALIDATED,
                                        value: '01'
                                ),
                                new RecordPortion(header: 'PersonBirthDate',
                                        status: ValidationStatus.VALID,
                                        value: '1982-10-28',
                                        numberOfRulesValidatedAgainst: 1,
                                )
                        ]),
                ])
    }
}