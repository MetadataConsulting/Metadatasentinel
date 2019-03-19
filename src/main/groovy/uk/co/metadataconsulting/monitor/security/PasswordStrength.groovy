package uk.co.metadataconsulting.monitor.security

import groovy.transform.CompileStatic

@CompileStatic
class PasswordStrength {
    public static final int DEFAULT_MIN_LENGTH = 8
    public static final int DEFAULT_MAX_LENGTH = 64
    static final String DEFAULT_PASSWORD_VALIDATION_REGEX = '^.*(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&]).*$'

    static boolean valid(String password) {
        if (!checkPasswordMinLength(password) ||
                !checkPasswordMaxLength(password) ||
                !checkPasswordRegex(password)) {
            return false
        }
        true
    }

    static boolean checkPasswordMinLength(String password) {
        int minLength = DEFAULT_MIN_LENGTH
        password && password.length() >= minLength
    }

    static boolean checkPasswordMaxLength(String password) {
        int maxLength = DEFAULT_MAX_LENGTH
        password && password.length() <= maxLength
    }

    static boolean checkPasswordRegex(String password) {
        password && password.matches(DEFAULT_PASSWORD_VALIDATION_REGEX)
    }
}
