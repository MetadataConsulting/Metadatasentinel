package uk.co.metadataconsulting.monitor

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