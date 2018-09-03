package uk.co.metadataconsulting.monitor

import com.stehno.ersatz.ContentType
import com.stehno.ersatz.Encoders
import com.stehno.ersatz.ErsatzServer
import geb.spock.GebSpec
import grails.testing.mixin.integration.Integration
import okhttp3.*
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.IgnoreIf
import spock.lang.Unroll
import uk.co.metadataconsulting.monitor.geb.LoginPage
import uk.co.metadataconsulting.monitor.geb.RecordCollectionIndexPage
import uk.co.metadataconsulting.monitor.geb.RecordCollectionRowModule
import uk.co.metadataconsulting.monitor.geb.RecordCollectionShowPage
import uk.co.metadataconsulting.monitor.security.MdxAuthenticationProvider

@Integration
class RecordCollectionControllerIntegrationSpec extends GebSpec implements LoginAs {

    RecordPortionMappingGormDataService recordPortionMappingGormDataService
    RecordCollectionGormService recordCollectionGormService
    RecordGormService recordGormService
    RuleFetcherService ruleFetcherService
    MdxAuthenticationProvider mdxAuthenticationProvider

    @Unroll
    @IgnoreIf({ !sys['geb.env'] })
    def "validate rules from networks"() {
        given:
        def authentication = SecurityContextHolder.context.authentication
        loginAs('supervisor', 'supervisor')

        final String gormUrl = 'gorm://org.modelcatalogue.core.DataElement:53'
        ErsatzServer ersatz = new ErsatzServer()
        String creds = Credentials.basic('supervisor', 'supervisor')
        ersatz.expectations {
            get('/api/modelCatalogue/core/validationRule/rules') {
                query('gormUrl', "${gormUrl}")
                called 1
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
        RecordCollectionGormEntity recordCollection= recordCollectionGormService.save(new RecordCollectionMetadataImpl(datasetName: "Test"))

        then:
        recordCollectionGormService.count() == old(recordCollectionGormService.count()) + 1

        when:
        RecordGormEntity validRecord = recordGormService.save(recordCollection, [new RecordPortion(header: 'NHS Number', value: '1848397860', numberOfRulesValidatedAgainst: 0)])
        RecordGormEntity invalidRecord = recordGormService.save(recordCollection, [new RecordPortion(header: 'NHS Number', value: '1234567890', numberOfRulesValidatedAgainst: 0)])

        then:
        recordGormService.count() == old(recordGormService.count()) + 2

        when:
        RecordCollectionMappingEntryGormEntity mapping = recordPortionMappingGormDataService.save('NHS Number', recordCollection)

        then:
        recordPortionMappingGormDataService.count() == old(recordPortionMappingGormDataService.count()) + 1

        when:
        recordPortionMappingGormDataService.update(mapping.id, gormUrl)

        then:
        recordPortionMappingGormDataService.count() == old(recordPortionMappingGormDataService.count())

        when:
        via RecordCollectionIndexPage

        then:
        at LoginPage

        when:
        LoginPage loginPage = browser.page LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at RecordCollectionIndexPage

        when: 'you check the rows displayed'
        RecordCollectionIndexPage indexPage = browser.page RecordCollectionIndexPage
        RecordCollectionRowModule recordCollectionRowModule = indexPage.rows[0]

        then: 'every cell dateCreated, createdBy, updatedBy, lastUpdated is populated '
        recordCollectionRowModule.datesetname().trim()
        recordCollectionRowModule.createdBy().trim()
        recordCollectionRowModule.datesetname().trim()
        recordCollectionRowModule.dateCreated().trim()
        recordCollectionRowModule.updatedBy().trim()
        recordCollectionRowModule.lastUpdated().trim()

        when:
        browser.to RecordCollectionShowPage, recordCollection.id

        then:
        at RecordCollectionShowPage

        when:
        RecordCollectionShowPage recordCollectionShowPage = browser.page(RecordCollectionShowPage)

        then:
        recordCollectionShowPage.createdBy().trim()
        recordCollectionShowPage.updatedBy().trim()

        when:
        recordCollectionShowPage.validate()

        then:
        recordCollectionShowPage.alertInfo().contains('Record Collection validation triggered')

        when:
        RecordGormEntity recordGormEntity = recordGormService.findById(validRecord.id, ['portions'])

        then:
        recordGormEntity
        recordGormEntity.portions.each { RecordPortionGormEntity recordPortionGormEntity ->
            assert recordPortionGormEntity.status == ValidationStatus.VALID
        }

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
