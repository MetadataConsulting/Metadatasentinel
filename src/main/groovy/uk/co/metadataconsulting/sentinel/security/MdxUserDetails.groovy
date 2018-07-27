package uk.co.metadataconsulting.sentinel.security

import groovy.transform.CompileStatic
import org.springframework.security.core.userdetails.UserDetails

@CompileStatic
class MdxUserDetails implements UserDetails {

    String password
    String username
    boolean accountNonExpired = true
    boolean accountNonLocked = true
    boolean credentialsNonExpired = true
    boolean enabled = true
    List<MdxAuthority> authorities = []

    @Override
    boolean isAccountNonExpired() {
        this.accountNonExpired
    }

    @Override
    boolean isAccountNonLocked() {
        this.accountNonLocked
    }

    @Override
    boolean isCredentialsNonExpired() {
        this.credentialsNonExpired
    }

    @Override
    boolean isEnabled() {
        this.enabled
    }
}
