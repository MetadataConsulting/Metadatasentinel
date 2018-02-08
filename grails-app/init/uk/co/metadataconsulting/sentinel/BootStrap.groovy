package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
class BootStrap {

    CsvImportService csvImportService

    RuleFetcherService ruleFetcherService

    def init = { servletContext ->

        List<String> mapping = ruleFetcherService.mappingGormUrl()
        File f = new File('src/test/resources/DIDS_XMLExample_01.csv')
        InputStream inputStream = f.newInputStream()
        csvImportService.save(mapping, inputStream, 100)

    }
    def destroy = {
    }


}
