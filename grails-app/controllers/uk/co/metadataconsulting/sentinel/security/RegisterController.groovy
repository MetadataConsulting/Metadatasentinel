package uk.co.metadataconsulting.sentinel.security

import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@CompileStatic
class RegisterController {
    RegisterService registerService

    @CompileDynamic
    def index() {
        Map copy = [:] + (flash.chainedParams ?: [:])
        copy.remove 'controller'
        copy.remove 'action'

        render view: 'index', model: [command: new RegisterCommand(copy)]
    }

    def register(RegisterCommand command) {
        RegisterResponse registerResponse = registerService.callMDXRegister(command)
        // handle responses from MDX appropriately

        if (registerResponse.flash?.error) {
            if (registerResponse.flash?.error == "Registration is not enabled for this application") {
                flash.error = "Registration not enabled for MDX instance"
                return
            }

            else {
                flash.error = registerResponse.flash.error
                flash.chainedParams = registerResponse.flash.chainedParams
                redirect action: 'index'
                return
            }
        }

        else if (registerResponse.model?.command) { // There was an error in the validation of the command
            render view: 'index', model: [command: RegisterCommandShapeValidationErrors.of(registerResponse.model.command)]
            return
        }
        else if (registerResponse.model?.emailSent) {
            render view: 'index', model: [emailSent: true]
        }

        else {
            flash.error = "MDX Response not recognized"
            render view: 'index', model: [command: command]
        }




//
//        if (!grailsApplication.config.mc.allow.signup) {
//            flash.error = "Registration is not enabled for this application"
//            return
//        }
//        params.remove 'format'
//
//        if (command.hasErrors()) {
//            render view: 'index', model: [command: command]
//            return
//        }
//
//        def supervisorEmail = System.getenv(UserService.ENV_SUPERVISOR_EMAIL)
//        boolean shouldUserBeDisabled = (supervisorEmail && command.email != supervisorEmail)
//        boolean enabled = !shouldUserBeDisabled
//        if ( shouldUserBeDisabled ) {
//            // notify admin
//            transactionalEmailService.sendEmail(supervisorEmail,
//                    conf.ui.register.emailFrom,
//                    "Metadata Registry - new user",
//                    "New user registered to your Metadata Registry. Please enable that account in user administration.")
//        }
//
//        RegistrationCode registrationCode = registerService.register(command.username, command.password, command.email, enabled)
//        if (registrationCode == null || registrationCode.hasErrors()) {
//            // null means problem creating the user
//            flash.error = message(code: 'spring.security.ui.register.miscError')
//            flash.chainedParams = params
//            redirect action: 'index'
//            return
//        }
//
//        String url = GenerateLink.generateLink('verifyRegistration', [t: registrationCode.token], grailsApplication)
//
//        def conf = SpringSecurityUtils.securityConfig
//        def body = conf.ui.register.emailBody
//        if (body.contains('$')) {
//            body = evaluate(body, [user: command, url: url])
//        }
//
//        transactionalEmailService.sendEmail(command.email,
//                conf.ui.register.emailFrom as String,
//                conf.ui.register.emailSubject as String,
//                body.toString())
//
//
//        render view: 'index', model: [emailSent: true]
    }
}

/**
 * Copied from SpringSecurityUI-1.0-RC2 Grails 2 plugin
 */
class PasswordValidators {

    static final passwordValidator = { String password, command ->
        if (command.username && command.username.equals(password)) {
            return 'command.password.error.username'
        }

        if (!checkPasswordMinLength(password, command) ||
                !checkPasswordMaxLength(password, command) ||
                !checkPasswordRegex(password, command)) {
            return 'command.password.error.strength'
        }
    }

    static boolean checkPasswordMinLength(String password, command) {
        def conf = SpringSecurityUtils.securityConfig

        int minLength = conf.ui.password.minLength instanceof Number ? conf.ui.password.minLength : 8

        password && password.length() >= minLength
    }

    static boolean checkPasswordMaxLength(String password, command) {
        def conf = SpringSecurityUtils.securityConfig

        int maxLength = conf.ui.password.maxLength instanceof Number ? conf.ui.password.maxLength : 64

        password && password.length() <= maxLength
    }

    static boolean checkPasswordRegex(String password, command) {
        def conf = SpringSecurityUtils.securityConfig

        String passValidationRegex = conf.ui.password.validationRegex ?:
                '^.*(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&]).*$'

        password && password.matches(passValidationRegex)
    }

    static final password2Validator = { value, command ->
        if (command.password != command.password2) {
            return 'command.password2.error.mismatch'
        }
    }
}

/**
 * RegisterCommand. Validation for User Registration Data. Copied from SpringSecurityUI-1.0-RC2 Grails 2 plugin
 */
class RegisterCommand {

    String username
    String email
    String password
    String password2
//
//    def grailsApplication
//
//    static constraints = {
//        username blank: false, validator: { value, command ->
//            if (value) {
//                def User = command.grailsApplication.getDomainClass(
//                        SpringSecurityUtils.securityConfig.userLookup.userDomainClassName).clazz
//                if (User.findByUsername(value)) {
//                    return 'registerCommand.username.unique'
//                }
//            }
//        }
//        email blank: false, email: true
//        password blank: false, validator: PasswordValidators.passwordValidator
//        password2 validator: PasswordValidators.password2Validator
//    }
}