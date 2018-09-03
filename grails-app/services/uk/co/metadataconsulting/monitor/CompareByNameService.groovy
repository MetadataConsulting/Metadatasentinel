package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic
import org.simmetrics.StringMetric
import org.simmetrics.metrics.StringMetrics

@CompileStatic
/**
 * Compares two strings with a fuzzy matching algorithm
 */
class CompareByNameService {

    Float distance(String source, String destination) {
        StringMetric metric = StringMetrics.jaroWinkler()
        new Float(metric.compare(source, destination))
    }
}