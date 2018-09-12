package uk.co.metadataconsulting.monitor.security

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import okhttp3.Credentials
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

@CompileStatic
@Slf4j
class MdxAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider implements GrailsConfigurationAware {
    private final Moshi moshi = new Moshi.Builder().build()
    private final OkHttpClient client = new OkHttpClient()
    private final JsonAdapter<UserCurrent> jsonAdapter = moshi.adapter(UserCurrent.class)

    String metadataUrl

    @Override
    void setConfiguration(Config co) {
        metadataUrl = co.getProperty('metadata.url', String)
        if (!metadataUrl || metadataUrl == '${METADATA_URL}') {
            metadataUrl = 'http://localhost:8080'
        }
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String username,
                                       UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        UserCurrent userCurrent = fetchUserCurrent(username, authentication.credentials as String)
        if (!userCurrent) {
            throw new UsernameNotFoundException("User $username not found".toString())
        }
        new MdxUserDetails(username: userCurrent.username,
                password: authentication.credentials as String,
                authorities: userCurrent.roles.collect {
                    new MdxAuthority(authority: it)
                })
    }

    protected UserCurrent fetchUserCurrent(String username, String password) {
        final String url = "${metadataUrl}/user/current".toString()
        String credential = Credentials.basic(username, password)
        HttpUrl httpUrl = HttpUrl.parse(url)
        if (!httpUrl) {
            log.error('unable to parse url: {}', url)
            return null
        }

        HttpUrl.Builder httpBuider = httpUrl.newBuilder()
        Request request = new Request.Builder()
                .url(httpBuider.build())
                .header("Authorization", credential)
                .header("Accept", 'application/json')
                .build()
        UserCurrent user
        try {
            Response response = client.newCall(request).execute()

            if ( response.isSuccessful()  ) {
                user = jsonAdapter.fromJson(response.body().source())
            } else {
                log.warn 'Response {}. Could not fetch UserCurrent at {}', response.code(), url
            }
            response.close()
        } catch (IOException ioexception) {
            log.warn('unable to connect to server {}', metadataUrl)
        }
        user
    }
}
