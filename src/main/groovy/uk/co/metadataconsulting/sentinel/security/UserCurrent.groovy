package uk.co.metadataconsulting.sentinel.security

import groovy.transform.CompileStatic

@CompileStatic
class UserCurrent {
    boolean success
    String username
    List<String> roles
    Long id
}
