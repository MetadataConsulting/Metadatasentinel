package uk.co.metadataconsulting.monitor.export

import builders.dsl.spreadsheet.api.BorderStyle
import builders.dsl.spreadsheet.builder.poi.PoiSpreadsheetBuilder
import org.springframework.context.MessageSource
import uk.co.metadataconsulting.monitor.RecordPortion
import uk.co.metadataconsulting.monitor.ValidationStatus

class ExcelExportService {

    MessageSource messageSource

    void export(OutputStream outs, RecordCollectionExportView view, ExportFormat format, Locale locale) {
        String msgAll = messageSource.getMessage("export.all".toString(), [] as Object[], 'All', locale)
        String msgNotValidated = messageSource.getMessage("export.notValidated".toString(), [] as Object[], 'Not Validated', locale)
        String msgValid = messageSource.getMessage("export.valid".toString(), [] as Object[], 'Valid', locale)
        String msgInvalid = messageSource.getMessage("export.invalid".toString(), [] as Object[], 'Invalid', locale)

        List<ExcelSheet> excelSheets  = [
                new ExcelSheet(sheetName: msgAll, headers: view.headers, rows: view.rows),
                new ExcelSheet(sheetName: msgNotValidated, headers: view.headers, rows: view.findNotValidatedRows()),
                new ExcelSheet(sheetName: msgValid, headers: view.headers, rows: view.findValidRows()),
                new ExcelSheet(sheetName: msgInvalid, headers: view.headers, rows: view.findInvalidRows()),
        ]
        PoiSpreadsheetBuilder.create(outs).build {
            for (ExcelSheet excelSheet : excelSheets) {
                sheet(excelSheet.sheetName) {
                    row {
                        for (String header : excelSheet.headers) {
                            cell {
                                value header
                                if (format == ExportFormat.XLSX) {
                                    colspan RecordPortion.toHeaderList().size()
                                }
                                style {
                                    background whiteSmoke
                                    align center center
                                    border right, {
                                        style BorderStyle.THICK
                                        color black
                                    }
                                    font {
                                        style bold
                                    }
                                }
                            }
                        }
                    }
                    if (excelSheet.rows) {
                        if (format == ExportFormat.XLSX) {
                            row {
                                List<String> recordPortionHeaders = excelSheet.rows.first().recordPortionList.collect {
                                    RecordPortion.toHeaderList()
                                }.flatten()
                                for (String recordPortionHeader : recordPortionHeaders) {
                                    cell {
                                        value messageSource.getMessage("export.header.${recordPortionHeader}".toString(),
                                                [] as Object[],
                                                recordPortionHeader,
                                                locale)

                                        style {
                                            background whiteSmoke
                                            if (recordPortionHeader == recordPortionHeaders[-1]) {
                                                border right, {
                                                    style BorderStyle.THICK
                                                    color black
                                                }
                                            }
                                            border bottom, {
                                                style BorderStyle.THIN
                                                color black
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        for (RecordCollectionExportRowView rowView : excelSheet.rows) {
                            row {
                                if (format == ExportFormat.XLSX) {

                                    for (RecordPortion recordPortion : rowView.recordPortionList) {
                                        int numberOfRecordPortions = recordPortion.toList().size()
                                        int countRecordPortion = 1
                                        for (String val : recordPortion.toList()) {
                                            cell {
                                                style {
                                                    if (recordPortion.status == ValidationStatus.VALID) {
                                                        font {
                                                            color green
                                                        }
                                                    }
                                                    if (recordPortion.status == ValidationStatus.INVALID) {
                                                        font {
                                                            color red
                                                        }
                                                    }
                                                    if (countRecordPortion == numberOfRecordPortions) {
                                                        style {
                                                            border right, {
                                                                style BorderStyle.THICK
                                                                color black
                                                            }
                                                        }
                                                    }
                                                    countRecordPortion++
                                                }
                                                value val
                                            }
                                        }
                                    }
                                } else if (format == ExportFormat.XLSX_COMPACT ) {
                                    for (RecordPortion recordPortion : rowView.recordPortionList) {
                                        cell {
                                            style {
                                                if (recordPortion.status == ValidationStatus.INVALID) {
                                                    font {
                                                        color red
                                                    }
                                                } else if (recordPortion.status == ValidationStatus.VALID) {
                                                    font {
                                                        color green
                                                    }
                                                }
                                                border right, {
                                                    style BorderStyle.THICK
                                                    color black
                                                }
                                            }
                                            value recordPortion.value
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}