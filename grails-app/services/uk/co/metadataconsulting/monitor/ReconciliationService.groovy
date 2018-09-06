package uk.co.metadataconsulting.monitor

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileStatic
import org.modelcatalogue.stringtransformer.CamelCaseSplitTransformer
import org.modelcatalogue.stringtransformer.DuplicatedDoubleQuotesToOneDoubleQuoteTransformer
import org.modelcatalogue.stringtransformer.LeadingTrailingDoubleQuotesRemovalTransformer
import org.modelcatalogue.stringtransformer.RemoveExtraSpaceTransformer
import org.modelcatalogue.stringtransformer.SnakeCaseToLowerCamelCaseTransformer
import org.modelcatalogue.stringtransformer.StringTransformer
import org.modelcatalogue.stringtransformer.ToLowerCaseTransformer
import uk.co.metadataconsulting.monitor.modelcatalogue.GormUrlName

@CompileStatic
/**
 * "Reconciles" a list of headers with a list of GormUrlNames using normalization + fuzzy matching.
 */
class ReconciliationService implements GrailsConfigurationAware {

    CompareByNameService compareByNameService

    /**
     * Threshold over which fuzzy matching succeeds
     */
    Float threshold
    /**
     * Maximum number of results to be returned from reconciliation
     */
    Integer max

    @Override
    void setConfiguration(Config co) {
        threshold = co.getProperty('reconciliation.threshold', Float, 0.6f)
        max = co.getProperty('reconciliation.max', Integer, 10)
    }
    /**
     * StringTransformers that normalize a string. Applied in the order listed.
     */
    List<StringTransformer> stringTransformerList = [
            new LeadingTrailingDoubleQuotesRemovalTransformer(),
            new DuplicatedDoubleQuotesToOneDoubleQuoteTransformer(),
            new CamelCaseSplitTransformer(), // This is here both before and after SnakeCaseToLowerCamelCaseTransformer because that flattens a lowerCamelCase if it's not snake case...
            new SnakeCaseToLowerCamelCaseTransformer(),
            new CamelCaseSplitTransformer(),
            new ToLowerCaseTransformer(),
            new RemoveExtraSpaceTransformer(),

    ] as List<StringTransformer>

    /**
     * Apply the StringTransformers
     * @param word
     * @return
     */
    String cleanup(String word) {
        String result = word
        for (StringTransformer transformer : stringTransformerList) {
            result = transformer.transform(result)
        }
        result
    }

    /**
     * Reconcile a list of GormUrlNames with a list of headers
     * @param catalogueElements
     * @param headers
     * @return
     */
    Map<String, List<GormUrlName>> reconcile(List<GormUrlName> catalogueElements, List<String> headers) {
        reconcile(catalogueElements, headers, this.threshold)
    }

    /**
     * Reconcile a list of GormUrlNames with a list of headers given a threshold
     * @param catalogueElements
     * @param headers
     * @param threshold
     * @return
     */
    Map<String, List<GormUrlName>> reconcile(List<GormUrlName> catalogueElements, List<String> headers, Float threshold) {
        Map m = [:]

        List<GormUrlNameWithDistance> cleanupCatalogueElements = catalogueElements.collect { GormUrlName gormUrlName ->
            new GormUrlNameWithDistance(
                    gormUrl: gormUrlName.gormUrl,
                    name:  gormUrlName.name,
                    dataModelId: gormUrlName.dataModelId,
                    nameCleanup: cleanup(gormUrlName.name))
        }
        if (headers) {
            for (String header : headers) {
                m[header] = reconcile(cleanupCatalogueElements, cleanup(header), threshold)
            }
        }
        m
    }

    /**
     * Return the first {@link #max} elements of the list of GormUrlNames, which has been filtered by: whether their fuzzy match score with the header value is over the threshold, and sorted by: that score.
     * @param gunsWithDistances
     * @param value
     * @param threshold
     * @return
     */
    List<GormUrlName> reconcile(List<GormUrlNameWithDistance> gunsWithDistances, String value, Float threshold) {

        gunsWithDistances?.each {GormUrlNameWithDistance gormUrlName ->
            gormUrlName.distance =  compareByNameService.distance(gormUrlName.nameCleanup, value)
        }
        if (threshold != null) {
            gunsWithDistances = gunsWithDistances?.findAll { it.distance > threshold }
        }
        gunsWithDistances?.sort { a, b ->
            int compare = b.distance <=> a.distance
            if ( compare != 0) {
                return compare
            }
            return 0
        }

        if(!gunsWithDistances) {
            return []
        }

        if (gunsWithDistances.size() < max.intValue()) {
            return gunsWithDistances as List<GormUrlName>
        }
        gunsWithDistances.subList(0, max.intValue()) as List<GormUrlName>
    }
}