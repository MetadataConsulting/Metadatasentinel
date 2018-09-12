package org.modelcatalogue.stringtransformer

import groovy.transform.CompileStatic

@CompileStatic
class CamelCaseSplitTransformer implements StringTransformer {

    @Override
    String transform(String word) {
        if (!word) {
            return word
        }
        word.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        )
    }
}
