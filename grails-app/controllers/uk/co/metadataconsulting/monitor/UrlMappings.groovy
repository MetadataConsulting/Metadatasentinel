package uk.co.metadataconsulting.monitor

class UrlMappings {

    static mappings = {
        "/"(controller: 'validationTask', action: 'index')

        "/recordCollection/index"(controller: 'recordCollection', action: 'index')
        "/recordCollection/cloneMapping"(controller: 'recordCollection', action: 'cloneMapping')
        "/recordCollection/cloneSave"(controller: 'recordCollection', action: 'cloneSave', httpMethod: 'POST')
        "/recordCollection/validate"(controller: 'recordCollection', action: 'validate')
        "/recordCollection/regenerateMapping"(controller: 'recordCollection', action: 'regenerateMapping')
        "/recordCollection/export"(controller: 'recordCollection', action: 'export')
        "/recordCollection/delete"(controller: 'recordCollection', action: 'delete', httpMethod: 'POST')
        "/recordCollection/$recordCollectionId/mapping"(controller: 'recordCollection', action: 'headersMapping')
        "/recordCollection/edit"(controller: 'recordCollection', action: 'edit')
        "/recordCollection/update"(controller: 'recordCollection', action: 'update')
        "/recordCollectionMapping/catalogueElements/$dataModelId"(controller: 'recordCollectionMapping', action: 'catalogueElements')
        "/recordCollectionMapping/$recordPortionMappingId/save"(controller: 'recordCollectionMapping', action: 'saveMapping')
        "/recordCollectionMapping/update"(controller: 'recordCollectionMapping', action: 'update', httpMethod: 'POST')
        "/import"(controller: 'recordCollection', action: 'importCsv')
        "/upload"(controller: 'recordCollection', action: 'uploadCsv', httpMethod: 'POST')

        "/importForNewValidationTask"(controller: 'validationTask', action: 'importCsv')
        "/uploadForNewValidationTask"(controller: 'validationTask', action: 'uploadCsv', httpMethod: 'POST')
        "/validationTask/index"(controller: 'validationTask', action: 'index')
        "/validationTask/$validationTaskId/show"(controller: 'validationTask', action: 'show')

        "/fetch/mdxSearch"(controller: 'MDXFetch', action: 'mdxSearch')

        "/records/$recordCollectionId"(controller: 'record', action: 'index')
        "/record/index"(controller: 'record', action: 'index')
        "/record/show"(controller: 'record', action: 'show')
        "/record/validate"(controller: 'record', action: 'validate')
        "/records/$recordCollectionId/$recordId"(controller: 'record', action: 'show')

        "/login/$action?/$id?(.$format)?"(controller: 'login')
        "/login/$action?/$id?(.$format)?"(controller: 'logout')

        "/register/index"(controller: 'register', action: 'index')
        "/register/register"(controller: 'register', action: 'register')

        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
