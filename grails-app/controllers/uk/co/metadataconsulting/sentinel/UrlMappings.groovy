package uk.co.metadataconsulting.sentinel

class UrlMappings {

    static mappings = {
        "/"(controller: 'recordCollection', action: 'index')
        "/recordCollection/validate"(controller: 'recordCollection', action: 'validate')
        "/import"(controller: 'recordCollection', action: 'importCsv')
        "/upload"(controller: 'recordCollection', action: 'uploadCsv')
        "/records/$recordCollectionId"(controller: 'record', action: 'index')
        "/record/index"(controller: 'record', action: 'index')
        "/record/show"(controller: 'record', action: 'show')
        "/record/validate"(controller: 'record', action: 'validate')
        "/records/$recordCollectionId/$recordId"(controller: 'record', action: 'show')

        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
