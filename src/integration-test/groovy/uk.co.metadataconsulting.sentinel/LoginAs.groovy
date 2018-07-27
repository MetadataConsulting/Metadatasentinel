package uk.co.metadataconsulting.sentinel

trait LoginAs {

    Map successLogin() {
        [
                success   : true,
                username  : 'supervisor',
                roles     : [ "ROLE_SUPERVISOR",
                              "ROLE_ADMIN",
                              "ROLE_METADATA_CURATOR",
                              "ROLE_USER"],
                id       : 85448
        ]
    }
}