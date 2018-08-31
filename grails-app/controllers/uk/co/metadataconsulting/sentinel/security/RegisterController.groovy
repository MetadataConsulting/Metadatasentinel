package uk.co.metadataconsulting.sentinel.security

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.springframework.context.MessageSource

@CompileStatic
class RegisterController {
    RegisterService registerService

    MessageSource messageSource

    static allowedMethods = [index: 'GET',
                             register: 'POST']

    @CompileDynamic
    def index() {
        render view: 'index', model: [command: new RegisterCommand()]
    }

    def register(RegisterCommand command) {
        if (command.hasErrors()) {
            render view: 'index', model: [command: command]
            return
        }

        RegisterResponse registerResponse = registerService.register(command)
        registerResponse?.with {
            username = command.username
            email = command.email
            password = command.password
            repeatPassword = command.repeatPassword
        }
        if (registerResponse.hasErrors()) {
            render view: 'index', model: [command: registerResponse]
            return
        }

        flash.message = messageSource.getMessage('sentinel.register.sent',
                [] as Object[],
                "Your account registration email was sent - check your mail!",
                request.locale)
        render view: 'index'
    }
}