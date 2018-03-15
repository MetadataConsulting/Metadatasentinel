package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class RecordPortionMappingGormEntity {
    String header
    String gormUrl
    Date dateCreated
    Date lastUpdated

    static belongsTo = [recordCollectionMapping: RecordCollectionMappingGormEntity]

    static constraints = {
        header nullable: false
        gormUrl nullable: true
    }

    static mapping = {
        table 'recordportionmapping'
    }
}
