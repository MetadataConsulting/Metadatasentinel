package uk.co.metadataconsulting.sentinel.security

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import grails.validation.ValidationErrors
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import uk.co.metadataconsulting.sentinel.MDXApiService

class RegisterService extends MDXApiService {

    private final JsonAdapter<RegisterResponse> registerResponseJsonAdapter = moshi.adapter(RegisterResponse.class)

    RegisterResponse callMDXRegister(RegisterCommand command) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, """
            {
                username: ${command.username},
                email: ${command.email},
                password: ${command.password},
                password2: ${command.password2}
            }
        """)
        final String url = "${metadataUrl}/api/modelCatalogue/register".toString()
//        final String credential = basic()
        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder()
        Request request = new Request.Builder()
                .url(httpBuider.build())
//                .header("Authorization", credential)
                .header("Accept", 'application/json')
                .post(body)
                .build()

        try {
            Response response = client.newCall(request).execute()

            if ( response.isSuccessful()  ) {

//                MDXSearchResponse mdxSearchResponse = mdxSearchResponseJsonAdapter.fromJson(response.body().source())
//                return mdxSearchResponse
                RegisterResponse registerResponse = registerResponseJsonAdapter.fromJson(response.body().source().inputStream().getText() + "}}}}")
                return registerResponse

            } else {
                log.warn 'Response {}. Could not fetch Data Models at {}', response.code(), url
            }
            response.close()
        } catch (IOException ioexception) {
            log.warn('unable to connect to server {}', metadataUrl)
        }
    }
}

/**
 * Corresponds to expected JSON {
 *     ?flash: Flash,
 *     ?model: Model
 * }
 * type Flash = {
 *     error: string,
 *     ?chainedParams: Map<String, String>
 * }
 * type Model = {
 *     ?command: RegisterCommand,
 *     ?emailSent: boolean
 * }
 * @param command
 * @return
 */
class RegisterResponse {
    Flash flash
    Model model
}

class Flash {
    String error
    Map<String, String> chainedParams
}

class Model {
    RegisterCommandShape command
    Boolean emailSent
}

/**
 * Needed because RegisterCommand itself has an extra field added to it by Grails.
 */
class RegisterCommandShape {
    String username
    String email
    String password
    String password2
    Errors errors
    GrailsApplicationJson grailsApplication
}

class RegisterCommandShapeValidationErrors {
    String username
    String email
    String password
    String password2
    ValidationErrors errors
    GrailsApplicationJson grailsApplication

    static RegisterCommandShapeValidationErrors of(RegisterCommandShape registerCommandShape) {
        return new RegisterCommandShapeValidationErrors(
                username: registerCommandShape.username,
                email: registerCommandShape.email,
                password: registerCommandShape.password,
                password2: registerCommandShape.password2,
                errors: new ValidationErrors(registerCommandShape.errors),
                grailsApplication: registerCommandShape.grailsApplication

        )
    }
}

class Errors {
    List<Error> errors
}

class Error {
    String object
    String field
    @Json(name = "rejected-value") String rejectedValue
    String message
}

class GrailsApplicationJson {
    List<String> allArtefacts
    List<String> allClasses
    List<ArtefactHandlerShape> artefactHandlers
}

class ArtefactHandlerShape {
    String pluginName
    String type
    @Json(name = "class") String className
}