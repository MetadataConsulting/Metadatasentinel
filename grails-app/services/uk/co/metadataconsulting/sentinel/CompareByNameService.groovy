package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import org.simmetrics.StringMetric
import org.simmetrics.metrics.StringMetrics

@CompileStatic
class CompareByNameService {

    Float distance(String source, String destination) {
        StringMetric metric = StringMetrics.levenshtein()
        new Float(metric.compare(source, destination))
    }
}