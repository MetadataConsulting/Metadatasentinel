package uk.co.metadataconsulting.sentinel

import com.stehno.ersatz.ContentType
import com.stehno.ersatz.Encoders
import com.stehno.ersatz.ErsatzServer
import geb.spock.GebSpec
import grails.testing.mixin.integration.Integration
import okhttp3.Credentials
import okhttp3.HttpUrl
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.IgnoreIf
import spock.lang.Shared
import spock.lang.Specification
import uk.co.metadataconsulting.sentinel.geb.LoginPage
import uk.co.metadataconsulting.sentinel.geb.RecordCollectionIndexPage
import uk.co.metadataconsulting.sentinel.geb.RecordCollectionShowPage
import uk.co.metadataconsulting.sentinel.geb.RecordShowPage
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules
import uk.co.metadataconsulting.sentinel.security.MdxAuthenticationProvider

@Integration
class RecordControllerIntegrationSpec extends GebSpec implements LoginAs {

    RecordPortionMappingGormDataService recordPortionMappingGormDataService
    RecordCollectionGormService recordCollectionGormService
    RecordGormService recordGormService
    RuleFetcherService ruleFetcherService
    MdxAuthenticationProvider mdxAuthenticationProvider

    @IgnoreIf({ !sys['geb.env'] })
    def "validate rules from networks"() {
        given:
        def authentication = SecurityContextHolder.context.authentication
        loginAs('supervisor', 'supervisor')

        final String gormUrl = 'gorm://org.modelcatalogue.core.DataElement:53'
        ErsatzServer ersatz = new ErsatzServer()
        String creds = Credentials.basic(ruleFetcherService.metadataUsername, ruleFetcherService.metadataApiKey)
        ersatz.expectations {
            get('/api/modelCatalogue/core/validationRule/rules') {
                query('gormUrl', "${gormUrl}")
                called 2
                header("Accept", 'application/json')
                header('Authorization', creds)
                responder {
                    encoder(ContentType.APPLICATION_JSON, Map, Encoders.json)
                    code(200)
                    content([
                            gormUrl   : 'gorm://org.modelcatalogue.core.DataElement:53',
                            name      : 'DIDS_V2b.xlsx: - NHS Number',
                            rules     : [],
                            url       : 'http://localhost:8080/#/15/dataElement/45',
                            validating: [
                                    bases: [],
                                    explicitRule: null,
                                    implicitRule: "def isValid = false \n                if (x.size() == 10) { \n                Integer total = 0\n                Integer i = 0\n                for (i = 0; i <= 8; i++) { \n                def digit = x.substring(i, (i+1)) \n                def factor = 10 - i \n                total = total + (digit.toInteger() * factor) }  \n                def checkDigit = (11 - (total.mod(11))) \n                if (checkDigit == 11) { checkDigit = 0 } \n                def check = x.substring(9,10)  \n                if (check.toInteger() == checkDigit && checkDigit!=10) { isValid = true } \n                }  \n                return isValid"
                            ]
                    ], ContentType.APPLICATION_JSON)
                }
            }
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
        ruleFetcherService.metadataUrl = ersatz.httpUrl
        mdxAuthenticationProvider.metadataUrl = ersatz.httpUrl

        when:
        RecordCollectionGormEntity recordCollection= recordCollectionGormService.save("Test")

        then:
        recordCollectionGormService.count() == old(recordCollectionGormService.count()) + 1

        when:
        RecordGormEntity validRecord = recordGormService.save(recordCollection, [new RecordPortion(header: 'NHS Number', value: '1848397860', numberOfRulesValidatedAgainst: 0)])
        RecordGormEntity invalidRecord = recordGormService.save(recordCollection, [new RecordPortion(header: 'NHS Number', value: '1234567890', numberOfRulesValidatedAgainst: 0)])

        then:
        recordGormService.count() == old(recordGormService.count()) + 2

        when:
        RecordCollectionMappingGormEntity mapping = recordPortionMappingGormDataService.save('NHS Number', recordCollection)

        then:
        recordPortionMappingGormDataService.count() == old(recordPortionMappingGormDataService.count()) + 1

        when:
        recordPortionMappingGormDataService.update(mapping.id, gormUrl, null)

        then:
        recordPortionMappingGormDataService.count() == old(recordPortionMappingGormDataService.count())

        when:
        RecordGormEntity recordGormEntity = recordGormService.findById(validRecord.id, ['portions'])

        then:
        recordGormEntity
        recordGormEntity.portions.each { RecordPortionGormEntity recordPortionGormEntity ->
            assert recordPortionGormEntity.status == ValidationStatus.NOT_VALIDATED
        }

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
        browser.to RecordShowPage, recordCollection.id, validRecord.id

        then:
        at RecordShowPage

        when:
        RecordShowPage recordShowPage = browser.page(RecordShowPage)
        recordShowPage.validate()
        recordGormEntity = recordGormService.findById(validRecord.id, ['portions'])

        then:
        recordGormEntity.portions.each { RecordPortionGormEntity recordPortionGormEntity ->
            assert recordPortionGormEntity.status == ValidationStatus.VALID
        }

        when:
        recordGormEntity = recordGormService.findById(invalidRecord.id, ['portions'])

        then:
        recordGormEntity.portions.each { RecordPortionGormEntity recordPortionGormEntity ->
            assert recordPortionGormEntity.status == ValidationStatus.NOT_VALIDATED
        }

        when:
        browser.to RecordShowPage, recordCollection.id, invalidRecord.id

        then:
        at RecordShowPage

        when:
        recordShowPage = browser.page(RecordShowPage)
        recordShowPage.validate()

        then:
        noExceptionThrown()

        when:
        recordGormEntity = recordGormService.findById(invalidRecord.id, ['portions'])

        then:
        recordGormEntity
        recordGormEntity.portions.each { RecordPortionGormEntity recordPortionGormEntity ->
            assert recordPortionGormEntity.status == ValidationStatus.INVALID
        }

        and:
        ersatz.verify()

        cleanup:
        ersatz.stop()
        if ( recordCollection ) {
            recordCollectionGormService.delete(recordCollection.id)
        }
        SecurityContextHolder.context.authentication = authentication
    }

}
