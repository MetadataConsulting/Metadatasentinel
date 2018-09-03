package uk.co.metadataconsulting.monitor

import grails.validation.Validateable

class SaveMappingCommand implements Validateable {
    Long recordPortionMappingId
    String gormUrl
    String name

    static constraints = {
        recordPortionMappingId nullable: false
        gormUrl nullable: false
        name nullable: false
    }
}