package uk.co.metadataconsulting.monitor.security

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class RegisterCommand implements Validateable, RegisterRequest {
    String username
    String email
    String password
    String repeatPassword

    static constraints = {
        username nullable: false, blank: false
        email nullable: false, blank: false
        password nullable: false, blank: false, validator: passwordValidator
        repeatPassword nullable: false, blank: false, validator: repeatPasswordValidator
    }

    static final Closure passwordValidator = { String password, RegisterCommand command ->
        if (command.username && command.username.equals(password)) {
            return 'command.password.error.username'
        }

        if (!PasswordStrength.valid(password)) {
            return 'command.password.error.strength'
        }
        true
    }

    static final Closure repeatPasswordValidator = { String value, RegisterCommand command ->
        if (command.password != command.repeatPassword) {
            return 'command.repeatPassword.error.mismatch'
        }
    }
}
