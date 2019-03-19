package uk.co.metadataconsulting.monitor.gorm

import grails.events.annotation.gorm.Listener
import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileStatic
import org.grails.datastore.mapping.engine.event.PreInsertEvent
import org.grails.datastore.mapping.engine.event.PreUpdateEvent
import uk.co.metadataconsulting.monitor.LoggedUsername
import uk.co.metadataconsulting.monitor.RecordCollectionGormEntity

@CompileStatic
class RecordCollectionGormListener implements LoggedUsername  {

    private final static String PROPERTY_UPDATED_BY = 'updatedBy'
    private final static String PROPERTY_CREATED_BY = 'createdBy'

    SpringSecurityService springSecurityService

    @Listener(RecordCollectionGormEntity)
    void onPreInsertEvent(PreInsertEvent event) {
        final String username = loggedUsername()
        if (username) {
            event.entityAccess.setProperty(PROPERTY_CREATED_BY, username)
            event.entityAccess.setProperty(PROPERTY_UPDATED_BY, username)
        }
    }

    @Listener(RecordCollectionGormEntity)
    void onPreUpdateEvent(PreUpdateEvent event) {
        final String username = loggedUsername()
        if (username) {
            event.entityAccess.setProperty(PROPERTY_UPDATED_BY, username)
        }
    }
}
