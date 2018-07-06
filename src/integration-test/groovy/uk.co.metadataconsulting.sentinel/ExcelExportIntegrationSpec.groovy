package uk.co.metadataconsulting.sentinel

import geb.spock.GebSpec
import grails.testing.mixin.integration.Integration
import spock.lang.IgnoreIf
import spock.lang.Unroll
import spock.util.concurrent.PollingConditions
import uk.co.metadataconsulting.sentinel.export.ExportFormat
import uk.co.metadataconsulting.sentinel.geb.RecordCollectionIndexPage
import uk.co.metadataconsulting.sentinel.geb.RecordCollectionShowPage

@Integration
class ExcelExportIntegrationSpec extends GebSpec {
    CsvImportService csvImportService
    RecordCollectionGormService recordCollectionGormService

    @Unroll
    @IgnoreIf({ !(sys['geb.env'] == 'chrome' || sys['geb.env'] == 'chromeHeadless') || !sys['download.folder'] } )
    def "verifies a file with #description can be exported for a record collection"(ExportFormat format, String extension, String description) {
        given:
        PollingConditions conditions = new PollingConditions(timeout: 30)
        String expectedFileDownloadPath = "${System.getProperty('download.folder')}/DIDS_XMLExample_20.$extension"

        final String filename = "src/test/resources/DIDS_XMLExample_20.csv"
        File f = new File(filename)

        expect:
        f.exists()

        when:
        csvImportService.save(f.newInputStream(), "DIDS_XMLExample_20", 50)

        then:
        noExceptionThrown()

        when:
        RecordCollectionIndexPage recordCollectionIndexPage = to RecordCollectionIndexPage
        recordCollectionIndexPage.rows[0].showRecordCollection()
        RecordCollectionShowPage recordCollectionShowPage = browser.page(RecordCollectionShowPage)

        recordCollectionShowPage.export(format)

        then:
        conditions.eventually { new File(expectedFileDownloadPath).exists() }

        cleanup:
        //recordCollectionGormService.deleteByDatasetName("DIDS_XMLExample_20")
        new File(expectedFileDownloadPath).delete()

        where:
        format                     | extension
        ExportFormat.XLSX_COMPACT  | 'xlsx'
        ExportFormat.XLSX          | 'xlsx'
        ExportFormat.CSV           | 'csv'

        description = format == ExportFormat.XLSX_COMPACT ? 'Compact XLSX' : extension
    }
}
