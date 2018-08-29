package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
/**
 * A single mapping entry for a record collection, mapping a header to a catalogue element.
 */
class RecordCollectionMappingGormEntity {

    /**
     * Header mapped from
     */
    String header

    /**
     * gormUrl of Catalogue Element mapped to, of format gorm://${FULLY_SPECIFIED_DOMAIN_CLASS_NAME}:${ID}
     */
    String gormUrl
    /**
     * Name of CatalogueElement mapped to
     */
    String name

    Date dateCreated
    Date lastUpdated

    static belongsTo = [recordCollection: RecordCollectionGormEntity]

    static constraints = {
        header nullable: false
        gormUrl nullable: true
        name nullable: true
    }

    static mapping = {
        table 'recordcollectionmapping'
    }
}
