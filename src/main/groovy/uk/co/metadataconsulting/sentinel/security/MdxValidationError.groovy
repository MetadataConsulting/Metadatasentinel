package uk.co.metadataconsulting.sentinel.security

import groovy.transform.CompileStatic

@CompileStatic
class MdxValidationError {

    String code
    String objectName
    String field
    Object rejectedValue
    String message
    String defaultMessage
}
