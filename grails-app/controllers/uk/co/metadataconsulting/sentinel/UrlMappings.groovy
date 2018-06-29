package uk.co.metadataconsulting.sentinel

class UrlMappings {

    static mappings = {
        "/"(controller: 'recordCollection', action: 'index')
        "/recordCollection/cloneMapping"(controller: 'recordCollection', action: 'cloneMapping')
        "/recordCollection/cloneSave"(controller: 'recordCollection', action: 'cloneSave', httpMethod: 'POST')
        "/recordCollection/validate"(controller: 'recordCollection', action: 'validate')
        "/recordCollection/exportValidExcel"(controller: 'recordCollection', action: 'exportValidExcel')
        "/recordCollection/delete"(controller: 'recordCollection', action: 'delete')
        "/recordCollection/$recordCollectionId/mapping"(controller: 'recordCollection', action: 'headersMapping')
        "/recordCollectionMapping/catalogueElements/$dataModelId"(controller: 'recordCollectionMapping', action: 'catalogueElements')
        "/recordCollectionMapping/update"(controller: 'recordCollectionMapping', action: 'update', httpMethod: 'POST')
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
