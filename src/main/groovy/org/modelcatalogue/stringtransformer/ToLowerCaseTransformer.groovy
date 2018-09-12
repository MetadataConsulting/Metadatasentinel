package org.modelcatalogue.stringtransformer

import groovy.transform.CompileStatic

@CompileStatic
class ToLowerCaseTransformer implements StringTransformer {

    @Override
    String transform(String word) {
        if (!word) {
           return word
        }
        word.toLowerCase()
    }
}
