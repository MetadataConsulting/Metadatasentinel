package uk.co.metadataconsulting.sentinel.security

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class RegisterCommandConstraintsSpec extends Specification {

    @Subject
    @Shared
    RegisterCommand cmd = new RegisterCommand()

    void 'username cannot be null'() {
        when:
        cmd.username = null

        then:
        !cmd.validate(['username'])
        cmd.errors['username'].code == 'nullable'
    }

    void 'password cannot be null'() {
        when:
        cmd.password = null

        then:
        !cmd.validate(['password'])
        cmd.errors['password'].code == 'nullable'
    }

    void 'repeatPassword cannot be null'() {
        when:
        cmd.repeatPassword = null

        then:
        !cmd.validate(['repeatPassword'])
        cmd.errors['repeatPassword'].code == 'nullable'
    }

    void 'email cannot be null'() {
        when:
        cmd.email = null

        then:
        !cmd.validate(['email'])
        cmd.errors['email'].code == 'nullable'
    }

    void 'username cannot be blank'() {
        when:
        cmd.username = ''

        then:
        !cmd.validate(['username'])
        cmd.errors['username'].code == 'blank'
    }

    void 'password cannot be blank'() {
        when:
        cmd.password = ''

        then:
        !cmd.validate(['password'])
        cmd.errors['password'].code == 'blank'
    }

    void 'repeatPassword cannot be blank'() {
        when:
        cmd.repeatPassword = ''

        then:
        !cmd.validate(['repeatPassword'])
        cmd.errors['repeatPassword'].code == 'blank'
    }

    void 'email cannot be blank'() {
        when:
        cmd.email = ''

        then:
        !cmd.validate(['email'])
        cmd.errors['email'].code == 'blank'
    }

    void 'password and repeatPassword must match'() {
        when:
        cmd.password = 'foo#123456'
        cmd.repeatPassword = 'foo#123456'

        then:
        cmd.validate(['repeatPassword'])

        when:
        cmd.password = 'foo#123456'
        cmd.repeatPassword = 'foo#12345689910'

        then:
        !cmd.validate(['repeatPassword'])
        cmd.errors['repeatPassword'].code == 'command.repeatPassword.error.mismatch'

    }
}
