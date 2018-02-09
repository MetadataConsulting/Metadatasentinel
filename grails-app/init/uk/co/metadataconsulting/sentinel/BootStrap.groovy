package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
class BootStrap {

    CsvImportService csvImportService

    def init = { servletContext ->

        //loadOnStartup()
    }
    def destroy = {
    }

    void loadOnStartup () {
        List<String> mapping = mappingGormUrl()
        println mapping.join(',')
        File f = new File('src/test/resources/DIDS_XMLExample_50000.csv')
        InputStream inputStream = f.newInputStream()
        csvImportService.save(mapping, inputStream, 100)
    }

    List<String> mappingGormUrl() {
        MAPPING.collect { Map m ->
            m.gormUrl
        }
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
