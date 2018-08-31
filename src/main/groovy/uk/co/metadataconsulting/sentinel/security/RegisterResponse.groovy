package uk.co.metadataconsulting.sentinel.security

import grails.validation.Validateable

class RegisterResponse implements Validateable {
    String username
    String email
    String password
    String repeatPassword
}
