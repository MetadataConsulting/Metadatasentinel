package uk.co.metadataconsulting.monitor.stringtransformer

import groovy.transform.CompileStatic

@CompileStatic
class DuplicatedDoubleQuotesToOneDoubleQuoteTransformer implements StringTransformer {

    @Override
    String transform(String word) {
        word.replaceAll('""', '"')
    }
}
