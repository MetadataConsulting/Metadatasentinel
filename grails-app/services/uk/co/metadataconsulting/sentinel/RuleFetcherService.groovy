package uk.co.metadataconsulting.sentinel

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import okhttp3.Credentials
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import uk.co.metadataconsulting.sentinel.modelcatalogue.CatalogueElements
import uk.co.metadataconsulting.sentinel.modelcatalogue.DataModels
import uk.co.metadataconsulting.sentinel.modelcatalogue.MDXSearchResponse
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules
import uk.co.metadataconsulting.sentinel.security.MdxUserDetails

@Slf4j
@CompileStatic
/**
 * Fetches not just ValidationRules but DataModels and CatalogueElements from the MDX.
 */
class RuleFetcherService extends MDXApiService {//implements GrailsConfigurationAware {

    private final JsonAdapter<ValidationRules> validationRulesJsonAdapter = moshi.adapter(ValidationRules.class)
    private final JsonAdapter<CatalogueElements> catalogueElementsJsonAdapter = moshi.adapter(CatalogueElements.class)
    private final JsonAdapter<DataModels> dataModelsJsonAdapter = moshi.adapter(DataModels.class)
    private final JsonAdapter<MDXSearchResponse> mdxSearchResponseJsonAdapter = moshi.adapter(MDXSearchResponse.class)
//
//    SpringSecurityService springSecurityService
//
//    /**
//     * URL of the associated MDX instance.
//     */
//    String metadataUrl
//
//    private String basic() {
//        if (springSecurityService.principal instanceof MdxUserDetails) {
//            MdxUserDetails mdxUserDetails = (MdxUserDetails) springSecurityService.principal
//            return Credentials.basic(mdxUserDetails.username, mdxUserDetails.password)
//        }
//        null
//    }
//
//    @Override
//    /**
//     * Set metadataUrl from config
//     */
//    void setConfiguration(Config co) {
//        metadataUrl = co.getProperty('metadata.url', String)
//        if ( !metadataUrl || metadataUrl == '${METADATA_URL}') {
//            metadataUrl = 'http://localhost:8080'
//        }
//    }

    MDXSearchResponse mdxSearch(MDXSearchCommand cmd) {
        final String url = "${metadataUrl}/api/modelCatalogue/core/catalogueElement/search?search=${cmd.query}&dataModel=${cmd.dataModelId}&searchImports=${cmd.searchImports.toString()}".toString()
        final String credential = basic()
        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder()
        Request request = new Request.Builder()
                .url(httpBuider.build())
                .header("Authorization", credential)
                .header("Accept", 'application/json')
                .build()

        try {
            Response response = client.newCall(request).execute()

            if ( response.isSuccessful()  ) {

                MDXSearchResponse mdxSearchResponse = mdxSearchResponseJsonAdapter.fromJson(response.body().source())
                return mdxSearchResponse

            } else {
                log.warn 'Response {}. Could not fetch Data Models at {}', response.code(), url
            }
            response.close()
        } catch (IOException ioexception) {
            log.warn('unable to connect to server {}', metadataUrl)
        }
    }

    DataModels fetchDataModels() {
        final String url = "${metadataUrl}/api/dashboard/dataModels".toString()
        final String credential = basic()
        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder()
        Request request = new Request.Builder()
                .url(httpBuider.build())
                .header("Authorization", credential)
                .header("Accept", 'application/json')
                .build()
        DataModels dataModels
        try {
            Response response = client.newCall(request).execute()

            if ( response.isSuccessful()  ) {
                dataModels = dataModelsJsonAdapter.fromJson(response.body().source())
            } else {
                log.warn 'Response {}. Could not fetch Data Models at {}', response.code(), url
            }
            response.close()
        } catch (IOException ioexception) {
            log.warn('unable to connect to server {}', metadataUrl)
        }
        dataModels
    }

    CatalogueElements fetchCatalogueElements(Long dataModelId) {
        final String url = "${metadataUrl}/api/dashboard/${dataModelId}/catalogueElements".toString()
        final String credential = basic()
        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder()
        Request request = new Request.Builder()
                .url(httpBuider.build())
                .header("Authorization", credential)
                .header("Accept", 'application/json')
                .build()
        CatalogueElements catalogueElements
        try {
            Response response = client.newCall(request).execute()

            if ( response.isSuccessful()  ) {
                catalogueElements = catalogueElementsJsonAdapter.fromJson(response.body().source())
            } else {
                log.warn 'Response {}. Could not fetch at {}', response.code(), url
            }
            response.close()
        } catch (IOException ioexception) {
            log.warn('unable to connect to server {}', metadataUrl)
        }
        catalogueElements
    }

    ValidationRules fetchValidationRules(String gormUrl) {

        final String url = "${metadataUrl}/api/modelCatalogue/core/validationRule/rules?gormUrl=${gormUrl}".toString()
        final String credential = basic()
        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder()
        Request request = new Request.Builder()
                .url(httpBuider.build())
                .header("Authorization", credential)
                .header("Accept", 'application/json')
                .build()
        ValidationRules validationRules

        try {
            Response response = client.newCall(request).execute()

            if ( response.isSuccessful()  ) {
                if ( response.code() == 200 ) {
                    validationRules = validationRulesJsonAdapter.fromJson(response.body().source())
                } else {
                    log.warn 'Response {}. no rules for gormUrl: {}', response.code(), gormUrl
                }
            } else {
                log.warn 'Response {}. Could not fetch at {}', response.code(), url
            }
            response.close()
        } catch (IOException ioexception) {
            log.warn('unable to connect to server {}', metadataUrl)
        }

        validationRules
    }

    Map<String,ValidationRules> fetchValidationRulesByMapping(List<RecordPortionMapping> recordPortionMappings) {
        Map<String, ValidationRules> m = [:]
        if ( recordPortionMappings ) {
            List<String> gormUrls = recordPortionMappings*.gormUrl.findAll { it != null && it != 'null' }
            for ( String gormUrl : gormUrls ) {
                ValidationRules validationRules = fetchValidationRules(gormUrl)
                if ( validationRules ) {
                    m[gormUrl] = validationRules
                }
            }
        }
        m
    }
}
