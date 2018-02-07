package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
class BootStrap {

    RecordCollectionGormService recordCollectionGormService
    RecordGormService recordGormService

    def init = { servletContext ->

        RecordCollectionGormEntity recordCollection = recordCollectionGormService.save()
        assert recordCollection

        List<String> metadataDomainEntityList = "NHSNumber,NHSNumberStatus,PersonBirthDate,PersonGenderCode,postalCode,GPCodeRegistration,PatientSourceSetting,ReferrerCode,ReferringOrgCode,DiagnosticTestReqDate,DiagnosticTestReqRecDate,ImagingCodeNICIP,ImagingCodeSNOMEDCT,DiagnosticTestDate,ImagingSiteCode,RadiologicalAccessionNumber".split(',') as List<String>

        List<String> rowOne = '"1234567890","01","1987-01-11","1","LS1 4HY","Y44680","01","C2918341","RR807","2011-09-09","2011-09-10","XMAMB","71651007","2011-10-09","RR807","RW6A06729288"'.split('","') as List<String>
        List<String> rowTwo = '"1234567890","01","1987-01-11","1","LS1 4HY","Y44680","01","C2918341","RR807","2011-09-09","2011-09-10","UGRAF","334531000000104","2011-10-10","RR807","RW6A06647291"'.split('","') as List<String>
        assert metadataDomainEntityList.size() == rowOne.size()
        assert metadataDomainEntityList.size() == rowTwo.size()


        for ( List<String> row : [rowOne, rowTwo] ) {

            List<RecordPortion> portionList = []
            for ( int i = 0; i < row.size(); i++ ) {
                String value = row[i]
                String metatadataDomainEntity = metadataDomainEntityList[i]
                portionList << new RecordPortion(value: value, metadataDomainEntity: metatadataDomainEntity, valid: true)
            }
            recordGormService.save(recordCollection, portionList)
        }

        assert recordGormService.countByRecordCollection(recordCollection) == 2

    }
    def destroy = {
    }
}
