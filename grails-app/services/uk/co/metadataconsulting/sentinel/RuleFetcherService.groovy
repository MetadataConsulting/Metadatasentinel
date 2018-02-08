package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRule
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules

@CompileStatic
class RuleFetcherService {

    List<String> mappingGormUrl() {
        MAPPING.collect { Map m ->
            m.gormUrl
        }
    }

    String xmlTagByGormUrl(String gormUrl) {
        List<Map<String, String>> l = MAPPING.findAll { Map<String, String> m ->
            m.gormUrl == gormUrl
        }
        if ( !l?.isEmpty() ) {
            return l.first().xmlTag
        }
        null
    }

    String nameByGormUrl(String gormUrl) {
        List<Map<String, String>> l = MAPPING.findAll { Map<String, String> m ->
            m.gormUrl == gormUrl
        }
        if ( !l?.isEmpty() ) {
            return l.first().name
        }
        null
    }

    ValidationRules fetchValidationRules(String gormUrl) {
        String xmlTag = xmlTagByGormUrl(gormUrl)
        String name = nameByGormUrl(gormUrl) ?: xmlTag
        List<ValidationRule> rules = []
        if ( xmlTag?.contains('Date') ) {
            String rule = '''
package uk.co.metadataconsulting.sentinel;

global uk.co.metadataconsulting.sentinel.RecordValidation record;

rule "date as yyyy-MM-dd"
   when
      eval(java.util.regex.Pattern.compile("([12]\\\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\\\d|3[01]))").matcher(record.getInput()[0]).matches())
   then
      record.setOutput(true);
end    
'''
            rules = [new ValidationRule(gormUrls: [gormUrl], name: 'Date must be yyyy-mm-dd', rule: rule)]
        }
        new ValidationRules(name: name, gormUrl: gormUrl, rules: rules)
    }


    public static final List<Map<String, String>> MAPPING = [
            [
                    name: 'DIDS:0.0.1 - NHS Number',
                    xmlTag: 'NHSNumber',
                    url: 'http://localhost:8080/#/14/dataElement/53',
                    gormUrl: 'gorm://org.modelcatalogue.core.DataElement:53',
            ],
            [
                    name: 'DIDS:0.0.1 - NHS Number Status',
                    xmlTag: 'NHSNumberStatus',
                    url: 'http://localhost:8080/#/14/dataElement/57',
                    gormUrl: 'gorm://org.modelcatalogue.core.DataElement:57',
            ],
            [
                    name: 'DIDS:0.0.1 - Date of Birth',
                    xmlTag: 'PersonBirthDate',
                    url: 'http://localhost:8080/#/14/dataElement/63',
                    gormUrl: 'gorm://org.modelcatalogue.core.DataElement:63',
            ],
            [
                    name: 'DIDS:0.0.1 - Patient Gender',
                    xmlTag: 'PersonGenderCode',
                    url: 'http://localhost:8080/#/14/dataElement/71',
                    gormUrl: 'gorm://org.modelcatalogue.core.DataElement:71',
            ],
            [
                    name: '',
                    xmlTag: 'postalCode',
                    url: '',
                    gormUrl: '',
            ],
            [
                    name: 'DIDS:0.0.1 - Patient Registered GP Practice',
                    xmlTag: 'GPCodeRegistration',
                    url: 'http://localhost:8080/#/14/dataElement/44',
                    gormUrl: 'gorm://org.modelcatalogue.core.DataElement:44',
            ],
            [
                    name: '',
                    xmlTag: 'PatientSourceSetting',
                    url: '',
                    gormUrl: '',
            ],
            [
                    name: 'DIDS:0.0.1 - Referrer',
                    xmlTag: 'ReferrerCode',
                    url: 'http://localhost:8080/#/14/dataElement/80',
                    gormUrl: 'gorm://org.modelcatalogue.core.DataElement:80',
            ],
            [
                    name: 'DIDS:0.0.1 - Referring Organisation',
                    xmlTag: 'ReferringOrgCode',
                    url: 'http://localhost:8080/#/14/dataElement/93',
                    gormUrl: 'gorm://org.modelcatalogue.core.DataElement:93',
            ],
            [
                    name: 'DIDS:0.0.1 - Date of Test Request',
                    xmlTag: 'DiagnosticTestReqDate',
                    url: 'http://localhost:8080/#/14/dataElement/27',
                    gormUrl: 'gorm://org.modelcatalogue.core.DataElement:27',
            ],
            [
                    name: 'DIDS:0.0.1 - Date Test Request Received',
                    xmlTag: 'DiagnosticTestReqRecDate',
                    url: 'http://localhost:8080/#/14/dataElement/33',
                    gormUrl: 'gorm://org.modelcatalogue.core.DataElement:33',
            ],
            [
                    name: 'DIDS:0.0.1 - Imaging Code (NICIP)',
                    xmlTag: 'ImagingCodeNICIP',
                    url: 'http://localhost:8080/#/14/dataElement/49',
                    gormUrl: 'gorm://org.modelcatalogue.core.DataElement:49',
            ],
            [
                    name: 'DIDS:0.0.1 - Imaging Code (SNOMED CT)',
                    xmlTag: 'ImagingCodeSNOMEDCT',
                    url: 'http://localhost:8080/#/14/dataElement/51',
                    gormUrl: 'gorm://org.modelcatalogue.core.DataElement:51',
            ],
            [
                    name: 'DIDS:0.0.1 - Date of Test',
                    xmlTag: 'DiagnosticTestDate',
                    url: 'http://localhost:8080/#/14/dataElement/16',
                    gormUrl: 'gorm://org.modelcatalogue.core.DataElement:16',
            ],
            [
                    name: 'DIDS:0.0.1 - Provider Site Code',
                    xmlTag: 'ImagingSiteCode',
                    url: 'http://localhost:8080/#/14/dataElement/103',
                    gormUrl: 'gorm://org.modelcatalogue.core.DataElement:103',
            ],
            [
                    name: 'DIDS:0.0.1 -  RIS Accession Number',
                    xmlTag: 'RadiologicalAccessionNumber',
                    url: 'http://localhost:8080/#/14/dataElement/77',
                    gormUrl: 'gorm://org.modelcatalogue.core.DataElement:77',
            ],
    ] as List<Map<String, String>>
}
