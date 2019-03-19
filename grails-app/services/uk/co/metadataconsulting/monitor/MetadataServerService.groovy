package uk.co.metadataconsulting.monitor

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

@Slf4j
@CompileStatic
class MetadataServerService implements GrailsConfigurationAware {

    String metadataUrl
    private final OkHttpClient client = new OkHttpClient()

    @Override
    void setConfiguration(Config co) {
        metadataUrl = co.getProperty('metadata.url', String)
        if (!metadataUrl || metadataUrl == '${METADATA_URL}') {
            metadataUrl = 'http://localhost:8080'
        }
    }

    void logMetadataServerStatus() {
        if (!metadataUrl) {
            log.warn('metadata.url not set')
        } else {
            final String url = "${metadataUrl}/user/current".toString()
            HttpUrl httpUrl = HttpUrl.parse(url)
            if (!httpUrl) {
                log.warn('could not parse {}', url)
            } else {
                HttpUrl.Builder httpBuider = httpUrl.newBuilder()
                Request request = new Request.Builder()
                        .url(httpBuider.build())
                        .head()
                        .build()
                try {
                    Response response = client.newCall(request).execute()
                    if(response.isSuccessful()) {
                        log.warn('could not hit to {}', url)
                    }
                } catch (IOException ioexception) {
                    log.warn('unable to connect to server {}', url)
                }
            }
        }
    }
}