package uk.co.metadataconsulting.sentinel

import grails.testing.services.ServiceUnitTest
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification
import spock.lang.Unroll
import uk.co.metadataconsulting.sentinel.modelcatalogue.GormUrlName

class ReconciliationServiceSpec extends Specification implements ServiceUnitTest<ReconciliationService> {

    Closure doWithSpring() {{ ->
        compareByNameService(CompareByNameService)
    }}

    @Unroll
    def "verify reconciliations for headers"() {
        given:
        Yaml yaml = new Yaml()
        File f = new File('src/test/resources/catalogueElements.yml')

        expect:
        f.exists()

        when:
        Map model = yaml.load(f.newDataInputStream())

        then:
        model['elements']
        model['elements'].size()

        when:
        List<GormUrlName> catalogueElements = model['elements'].collect { Map m ->
            new GormUrlName(gormUrl: m['gormUrl'], name: m['name'])
        }

        then:
        catalogueElements
        catalogueElements.size()

        when:
        List<String> headers = [
                'DiagnosticTestReqDate',
                'NHSNumber',
                'Empty',
                'NHSNumberStatus',
                'PersonBirthDate',
                'PersonGenderCode',
                'postalCode',
                'GPCodeRegistration',
                'PatientSourceSetting',
                'ReferrerCode',
                'ReferringOrgCode',
                'DiagnosticTestReqDate',
                'DiagnosticTestReqRecDate',
                'ImagingCodeNICIP',
                'ImagingCodeSNOMEDCT',
                'DiagnosticTestDate',
                'ImagingSiteCode',
                'RadiologicalAccessionNumber',
                'Color',
        ]

        Map<String, List<GormUrlName>> suggestions = service.reconcile(catalogueElements, headers)

        then:
        suggestions
        suggestions.get('NHSNumber').first().gormUrl == "gorm://org.modelcatalogue.core.DataElement:178666"
        suggestions.get('NHSNumber').first().name == "NHS Number"

        suggestions.get('ImagingCodeNICIP').first().gormUrl == "gorm://org.modelcatalogue.core.DataElement:178662"
        suggestions.get('ImagingCodeNICIP').first().name == "Imaging Code (NICIP)"
    }

    def "verify cleanup"() {
        expect:
        "date of test request" == service.cleanup("Date of Test Request")

    }
}