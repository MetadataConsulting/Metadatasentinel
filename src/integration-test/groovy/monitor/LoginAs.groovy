package uk.co.metadataconsulting.monitor

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder

trait LoginAs {

    Map successLogin() {
        [
                success   : true,
                username  : 'supervisor',
                roles     : [ "ROLE_SUPERVISOR",
                              "ROLE_ADMIN",
                              "ROLE_METADATA_CURATOR",
                              "ROLE_USER"],
                id       : 85448
        ]
    }

    void loginAs(String username, String password, String authority = null) {
        List<GrantedAuthority> authorityList = authority ? AuthorityUtils.createAuthorityList(authority) : []
        SecurityContextHolder.context.authentication = new UsernamePasswordAuthenticationToken(username,
                password,
                authorityList)
    }

}