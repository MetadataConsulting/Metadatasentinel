package uk.co.metadataconsulting.monitor


import groovy.transform.CompileStatic
import uk.co.metadataconsulting.monitor.modelcatalogue.MDXSearchResponse
import uk.co.metadataconsulting.monitor.modelcatalogue.MDXSearchResponseProjection

@CompileStatic
class MDXFetchController {

    RuleFetcherService ruleFetcherService

    static allowedMethods = [
            mdxSearch: 'GET'
    ]

    def mdxSearch(MDXSearchCommand cmd) {
        if (cmd.hasErrors()) {
            response.status = 400
            log.info("Bad request made for mdxSearch action")
            return
        }
        MDXSearchResponse mdxSearchResponse = ruleFetcherService.mdxSearch(cmd)
        respond MDXSearchResponseProjection.of(mdxSearchResponse)
    }
}