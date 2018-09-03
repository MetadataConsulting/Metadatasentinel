package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic
import groovy.transform.ToString
import uk.co.metadataconsulting.monitor.modelcatalogue.GormUrlName

@ToString(includeSuper = true, includes = ['distance', 'nameCleanup'], includePackage = false)
@CompileStatic
class GormUrlNameWithDistance extends GormUrlName {
    Float distance
    String nameCleanup
}
