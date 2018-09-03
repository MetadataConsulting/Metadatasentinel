package uk.co.metadataconsulting.monitor

import com.stehno.ersatz.ContentType
import com.stehno.ersatz.Encoders
import com.stehno.ersatz.ErsatzServer
import geb.spock.GebSpec
import grails.testing.mixin.integration.Integration
import okhttp3.Credentials
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.IgnoreIf
import spock.lang.Unroll
import spock.util.concurrent.PollingConditions
import uk.co.metadataconsulting.monitor.export.ExportFormat
import uk.co.metadataconsulting.monitor.geb.LoginPage
import uk.co.metadataconsulting.monitor.geb.RecordCollectionIndexPage
import uk.co.metadataconsulting.monitor.geb.RecordCollectionShowPage
import uk.co.metadataconsulting.monitor.security.MdxAuthenticationProvider

@Integration
class ExcelExportIntegrationSpec extends GebSpec implements LoginAs {
    CsvImportService csvImportService
    MdxAuthenticationProvider mdxAuthenticationProvider
    RecordCollectionGormService recordCollectionGormService

    @Unroll
    @IgnoreIf({ !(sys['geb.env'] == 'chrome' || sys['geb.env'] == 'chromeHeadless') || !sys['download.folder'] } )
    def "verifies a file with #description can be exported for a record collection"(ExportFormat format, String extension, String description) {
        given:
        def authentication = SecurityContextHolder.context.authentication
        loginAs('supervisor', 'supervisor')

        ErsatzServer ersatz = new ErsatzServer()
        ersatz.expectations {
            get('/user/current') {
                called 1
                header("Accept", 'application/json')
                header('Authorization', Credentials.basic('supervisor', 'supervisor'))
                responder {
                    encoder(ContentType.APPLICATION_JSON, Map, Encoders.json)
                    code(200)
                    content(successLogin(), ContentType.APPLICATION_JSON)
                }
            }
        }
        mdxAuthenticationProvider.metadataUrl = ersatz.httpUrl

        PollingConditions conditions = new PollingConditions(timeout: 5)
        String expectedFileDownloadPath = "${System.getProperty('download.folder')}/DIDS_XMLExample_20.$extension"

        final String filename = "src/test/resources/DIDS_XMLExample_20.csv"
        File f = new File(filename)

        expect:
        f.exists()

        when:
        RecordCollectionGormEntity recordCollectionEntity =  recordCollectionGormService.save(new RecordCollectionMetadataImpl(datasetName: "DIDS_XMLExample_20"))
        csvImportService.save(f.newInputStream(), 50, recordCollectionEntity)

        then:
        noExceptionThrown()

        when:
        via RecordCollectionIndexPage

        then:
        at LoginPage

        when:
        LoginPage loginPage = browser.page LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at RecordCollectionIndexPage

        when:
        RecordCollectionIndexPage recordCollectionIndexPage = browser.page RecordCollectionIndexPage
        recordCollectionIndexPage.rows[0].showRecordCollection()
        RecordCollectionShowPage recordCollectionShowPage = browser.page(RecordCollectionShowPage)

        recordCollectionShowPage.export(format)

        then:
        conditions.eventually { new File(expectedFileDownloadPath).exists() }

        and:
        ersatz.verify()

        cleanup:
        recordCollectionShowPage.delete()
        new File(expectedFileDownloadPath).delete()

        ersatz.stop()

        SecurityContextHolder.context.authentication = authentication

        where:
        format                     | extension
        ExportFormat.XLSX_COMPACT  | 'xlsx'
        ExportFormat.XLSX          | 'xlsx'
        ExportFormat.CSV           | 'csv'

        description = format == ExportFormat.XLSX_COMPACT ? 'Compact XLSX' : extension

    }
}
