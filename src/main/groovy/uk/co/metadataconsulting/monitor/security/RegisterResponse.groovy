package uk.co.metadataconsulting.monitor.security

import grails.validation.Validateable

class RegisterResponse implements Validateable {
    String username
    String email
    String password
    String repeatPassword
}
