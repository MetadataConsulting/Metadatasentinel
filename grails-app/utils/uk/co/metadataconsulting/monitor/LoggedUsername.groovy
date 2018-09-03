package uk.co.metadataconsulting.monitor

import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.springframework.security.core.userdetails.UserDetails

@CompileStatic
trait LoggedUsername {

    abstract SpringSecurityService getSpringSecurityService()

    @CompileDynamic
    String loggedUsername() {
        if (springSecurityService.principal instanceof String) {
            return springSecurityService.principal
        }
        if (springSecurityService.principal instanceof UserDetails) {
            return ((UserDetails) springSecurityService.principal).username
        }
        null
    }
}