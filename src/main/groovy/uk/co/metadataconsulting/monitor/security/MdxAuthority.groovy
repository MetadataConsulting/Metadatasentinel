package uk.co.metadataconsulting.monitor.security

import groovy.transform.CompileStatic
import org.springframework.security.core.GrantedAuthority

@CompileStatic
class MdxAuthority implements GrantedAuthority {
    String authority
}
