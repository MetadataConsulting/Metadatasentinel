package uk.co.metadataconsulting.sentinel.security

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import grails.plugin.json.builder.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.grails.web.json.JSONException
import org.grails.web.json.JSONObject

@Slf4j
class RegisterService implements GrailsConfigurationAware {
    private final OkHttpClient client = new OkHttpClient()
    private final Moshi moshi = new Moshi.Builder().build()
    private final JsonAdapter<MdxValidationErrors> validationErrorsJsonAdapter = moshi.adapter(MdxValidationErrors.class)
    private String registerUrl


    /**
     * Set metadataUrl from config
     * @param Configuration object
     */
    @Override
    void setConfiguration(Config co) {
        String metadataUrl = co.getProperty('metadata.url', String)
        if ( !metadataUrl || metadataUrl == '${METADATA_URL}') {
            metadataUrl = 'http://localhost:8080'
        }
        registerUrl = "${metadataUrl}/api/modelCatalogue/register"
    }

    private RequestBody registerBody(RegisterRequest req) {
        String json = JsonOutput.toJson([username: req.username,
                                         email: req.email,
                                         password: req.password,
        ])
        RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
    }

    private Request registerRequest(RegisterRequest req) {
        new Request.Builder()
                .url(HttpUrl.parse(registerUrl).newBuilder().build())
                .header("Accept", 'application/json')
                .header("Content-Type", 'application/json')
                .post(registerBody(req))
                .build()
    }

    RegisterResponse register(RegisterRequest req) {

        Request request = registerRequest(req)

        try {
            Response response = client.newCall(request).execute()

            if ( response.isSuccessful()  ) {
                return new RegisterResponse()

            } else {
                if (response.code() == 422) {
                    MdxValidationErrors validationErrors = validationErrorsJsonAdapter.fromJson(response.body().source())
                    RegisterResponse registerResponse = new RegisterResponse()
                    for (MdxValidationError error : validationErrors.errors) {
                        registerResponse.errors.reject(error.code, [] as Object[], error.message)
                    }
                    return registerResponse
                }
                log.warn 'Response {}. Could not fetch Data Models at {}', response.code(), request.url().uri().toString()
            }
            response.close()
        } catch (IOException ioexception) {
            log.warn('unable to connect to server {}', registerUrl)
        }
        RegisterResponse registerResponse = new RegisterResponse()
        registerResponse.errors.reject('register.failed.message', [] as Object[], "Registration failed")
        return registerResponse
    }
}
