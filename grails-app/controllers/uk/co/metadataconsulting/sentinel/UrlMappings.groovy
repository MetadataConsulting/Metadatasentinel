package uk.co.metadataconsulting.sentinel

class UrlMappings {

    static mappings = {
        "/"(controller: 'recordCollection', action: 'index')
        "/import"(controller: 'recordCollection', action: 'importCsv')
        "/upload"(controller: 'recordCollection', action: 'uploadCsv')
        "/records/$recordCollectionId"(controller: 'record', action: 'index')
        "/record/index"(controller: 'record', action: 'index')
        "/records/$recordCollectionId/$recordId"(controller: 'recordPortion', action: 'show')

        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
