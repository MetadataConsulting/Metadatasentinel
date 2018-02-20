package org.modelcatalogue.core.scripting

import groovy.transform.CompileStatic
import groovy.transform.ToString

@CompileStatic
class ValidatingImpl implements Validating {
    String implicitRule
    String explicitRule
    List<ValidatingImpl> bases

    String toString() {
        StringBuilder sb = new StringBuilder()
        if ( implicitRule  ) {
            sb.append('Implicit Rule: ')
            sb.append(implicitRule)
        }
        if ( explicitRule  ) {
            sb.append('Explicit Rule: ')
            sb.append(explicitRule)
        }

        if ( bases != null ) {
            for ( ValidatingImpl base : bases ) {
                sb.append(base.toString())
            }
        }
        sb.toString()
    }
}
