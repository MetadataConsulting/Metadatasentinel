package uk.co.metadataconsulting.sentinel.modelcatalogue

import groovy.transform.CompileStatic
import groovy.transform.ToString

@ToString(includes = ['name', 'gormUrl'], includePackage = false, includeNames = false)
@CompileStatic
class GormUrlName {
    String gormUrl
    String name
    Long dataModelId
}
