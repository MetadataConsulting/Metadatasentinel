package uk.co.metadataconsulting.sentinel

import com.squareup.moshi.Moshi
import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import grails.plugin.springsecurity.SpringSecurityService
import okhttp3.Credentials
import okhttp3.OkHttpClient
import org.springframework.security.core.userdetails.User
import uk.co.metadataconsulting.sentinel.security.MdxUserDetails

/**
 * Base Service for anything wishing to call the MDX
 */
class MDXApiService implements GrailsConfigurationAware {

    SpringSecurityService springSecurityService

    final Moshi moshi = new Moshi.Builder().build()
    final OkHttpClient client = new OkHttpClient()

    /**
     * URL of the associated MDX instance.
     */
    String metadataUrl

    String basic() {
        if (springSecurityService.principal instanceof MdxUserDetails) {
            MdxUserDetails mdxUserDetails = (MdxUserDetails) springSecurityService.principal
            return Credentials.basic(mdxUserDetails.username, mdxUserDetails.password)
        }
        else if (springSecurityService.principal instanceof User) {
            User user = (User) springSecurityService.principal
            return Credentials.basic(user.username, user.password)
        }
        null
    }

    @Override
    /**
     * Set metadataUrl from config
     */
    void setConfiguration(Config co) {
        metadataUrl = co.getProperty('metadata.url', String)
        if ( !metadataUrl || metadataUrl == '${METADATA_URL}') {
            metadataUrl = 'http://localhost:8080'
        }
    }
}