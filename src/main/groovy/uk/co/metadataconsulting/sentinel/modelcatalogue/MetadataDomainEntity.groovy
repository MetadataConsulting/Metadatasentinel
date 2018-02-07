package uk.co.metadataconsulting.sentinel.modelcatalogue

import groovy.transform.Canonical
import groovy.transform.CompileStatic

@Canonical
@CompileStatic
class MetadataDomainEntity {
    String domain
    Long id
    String name
}
