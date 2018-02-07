package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
class BootStrap {

    CsvImportService csvImportService

    def init = { servletContext ->

        String mapping = 'NHSNumber,NHSNumberStatus,PersonBirthDate,PersonGenderCode,postalCode,GPCodeRegistration,PatientSourceSetting,ReferrerCode,ReferringOrgCode,DiagnosticTestReqDate,DiagnosticTestReqRecDate,ImagingCodeNICIP,ImagingCodeSNOMEDCT,DiagnosticTestDate,ImagingSiteCode,RadiologicalAccessionNumber'
        File f = new File('src/test/resources/DIDS_XMLExample_01.csv')
        InputStream inputStream = f.newInputStream()
        csvImportService.save(mapping, inputStream, 100)

    }
    def destroy = {
    }
}
