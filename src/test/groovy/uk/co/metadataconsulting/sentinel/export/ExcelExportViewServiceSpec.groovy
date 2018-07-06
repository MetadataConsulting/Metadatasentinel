package uk.co.metadataconsulting.sentinel.export

import builders.dsl.spreadsheet.query.api.SpreadsheetCriteria
import builders.dsl.spreadsheet.query.api.SpreadsheetCriteriaResult
import builders.dsl.spreadsheet.query.poi.PoiSpreadsheetCriteria
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification
import spock.lang.Unroll

class ExcelExportViewServiceSpec extends Specification
        implements ServiceUnitTest<ExcelExportService>, RecordCollectionExportViewFixture {

    @Unroll
    def "verifies an Excel file is generated with four tabs"(ExportFormat format) {
        given:
        File f = new File("build/${format.toString()}.xlsx")
        OutputStream os = f.newOutputStream()
        RecordCollectionExportView view = recordCollectionExportView()
        when:
        service.export(os, view, format, Locale.ENGLISH)

        then:
        noExceptionThrown()

        when: 'if you check the All Sheet'
        SpreadsheetCriteria query = PoiSpreadsheetCriteria.FACTORY.forFile(f)
        SpreadsheetCriteriaResult result = query.query {
            sheet('All') {
                row {
                    cell {
                        value '00000000'
                    }
                }
            }
        }

        then: 'there are two rows with a cell of value 00000000'
        result.cells.size() == 2

        when:
        result = query.query {
            sheet('All') {
                row {
                    cell {
                        value '1234567890'
                    }
                }
            }
        }

        then: 'there is a row with a cell of value 1234567890'
        result.cells.size() == 1

        when: 'if you check the "Not Validated" Sheet'
        result = query.query {
            sheet('Not Validated') {
                row {
                    cell {
                        value '00000000'
                    }
                }
            }
        }

        then: 'there is one row with a cell of value 00000000'
        result.cells.size() == 1

        when: 'if you check the "Valid" Sheet'
        result = query.query {
            sheet('Valid') {
                row {
                    cell {
                        value '00000000'
                    }
                }
            }
        }

        then: 'there is one row with a cell of value 00000000'
        result.cells.size() == 1


        when: 'if you check the "Invalid" Sheet'
        result = query.query {
            sheet('Invalid') {
                row {
                    cell {
                        value '1234567890'
                    }
                }
            }
        }

        then: 'there is one row with a cell of value 1234567890'
        result.cells.size() == 1

        cleanup:
        os.close()
        f.delete()

        where:
        format << [ExportFormat.XLSX_COMPACT, ExportFormat.XLSX]
    }
}
