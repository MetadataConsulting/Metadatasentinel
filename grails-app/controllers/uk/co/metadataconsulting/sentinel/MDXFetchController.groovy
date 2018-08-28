package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

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
        return ruleFetcherService.mdxSearch(cmd)
    }
}