package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
class UpdateGormUrlRequest {
    Long id
    String gormUrl
    Long dataModelId
}
