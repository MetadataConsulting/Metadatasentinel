package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic

@CompileStatic
class UpdateGormUrlRequest {
    Long id
    String gormUrl
    Long dataModelId
}
