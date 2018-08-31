package uk.co.metadataconsulting.sentinel.security

interface RegisterRequest {
    String getUsername()
    String getEmail()
    String getPassword()
}
