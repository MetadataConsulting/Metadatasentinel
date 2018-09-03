package uk.co.metadataconsulting.monitor.security

interface RegisterRequest {
    String getUsername()
    String getEmail()
    String getPassword()
}
