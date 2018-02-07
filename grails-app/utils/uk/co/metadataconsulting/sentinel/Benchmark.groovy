package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
trait Benchmark {
    long benchmark(Closure cls) {
        long start = System.currentTimeMillis()
        cls.call()
        long now = System.currentTimeMillis()
        now - start
    }
}