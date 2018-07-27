package uk.co.metadataconsulting.sentinel.security

import groovy.transform.CompileStatic
import org.springframework.security.core.GrantedAuthority

@CompileStatic
class MdxAuthority implements GrantedAuthority {
    String authority
}
