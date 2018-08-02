package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import groovy.transform.ToString
import uk.co.metadataconsulting.sentinel.modelcatalogue.GormUrlName

@ToString(includeSuper = true, includes = ['distance', 'nameCleanup'], includePackage = false)
@CompileStatic
class GormUrlNameWithDistance extends GormUrlName {
    Float distance
    String nameCleanup
}
