package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
class PaginationQuery {
    Integer max
    Integer offset

    Map toMap() {
        [max: max, offset: offset]
    }
}
