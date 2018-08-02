package uk.co.metadataconsulting.sentinel

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
import uk.co.metadataconsulting.sentinel.modelcatalogue.GormUrlName

@CompileStatic
class ReconciliationService implements GrailsConfigurationAware {

    CompareByNameService compareByNameService

    Float threshold
    Integer max

    @Override
    void setConfiguration(Config co) {
        threshold = co.getProperty('reconciliation.threshold', Float, 0.6f)
        max = co.getProperty('reconciliation.max', Integer, 10)
    }

    List<StringTransformer> stringTransformerList = [
            new LeadingTrailingDoubleQuotesRemovalTransformer(),
            new DuplicatedDoubleQuotesToOneDoubleQuoteTransformer(),
            new SnakeCaseToLowerCamelCaseTransformer(),
            new CamelCaseSplitTransformer(),
            new ToLowerCaseTransformer(),
            new RemoveExtraSpaceTransformer(),

    ] as List<StringTransformer>

    String cleanup(String word) {
        String result = word
        for (StringTransformer transformer : stringTransformerList) {
            result = transformer.transform(result)
        }
        result
    }

    Map<String, List<GormUrlName>> reconcile(List<GormUrlName> catalogueElements, List<String> headers) {
        reconcile(catalogueElements, headers, this.threshold)
    }

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

    List<GormUrlName> reconcile(List<GormUrlNameWithDistance> l, String value, Float threshold) {

        l?.each {GormUrlNameWithDistance gormUrlName ->
            gormUrlName.distance =  compareByNameService.distance(gormUrlName.nameCleanup, value)
        }
        if (threshold != null) {
            l = l?.findAll { it.distance > threshold }
        }
        l?.sort { a, b ->
            int compare = b.distance <=> a.distance
            if ( compare != 0) {
                return compare
            }
            return 0
        }

        if(!l) {
            return []
        }

        if (l.size() < 10) {
            return l as List<GormUrlName>
        }
        l.subList(0, max.intValue()) as List<GormUrlName>
    }
}