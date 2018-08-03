<html>
<head>
    <title>Edit | Record Collection</title>
    <meta name="layout" content="main" />
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light sticky-top">
    <g:render template="/templates/navbarBrand"/>
    <g:render template="/templates/logout"/>
</nav>

<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><g:link controller="recordCollection" action="index"><g:message code="nav.home" default="Home"/></g:link></li>
        <li class="breadcrumb-item"><g:link controller="record" action="index" params="[recordCollectionId: recordCollectionId]">${recordCollectionEntity.datasetName}</g:link></li>
        <li class="breadcrumb-item active" aria-current="page"><g:message code="nav.recordcollection.edit" default="Edit Record Collection"/></li>
    </ol>
</nav>

<article>
<g:render template="/templates/flashmessage"/>
<g:render template="/templates/flasherror"/>

<g:form action="update" controller="recordCollection">
    <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
    <div class="form-group">
        <label for="datasetName"><g:message code="recordCollection.datasetName" default="Dataset Name"/>
            <g:textField name="datasetName" value="${recordCollectionEntity.datasetName}" id="datasetName" /></label>
    </div>
    <div class="form-group">
        <g:select noSelection="${['null':'Select One...']}"
                  optionKey="id"
                  optionValue="name"
                  name="dataModelId"
                  from="${dataModelList}"
                  value="${recordCollectionEntity.dataModelId}" />
    </div>
    <div class="form-group">
        <label for="about"><g:message code="recordCollection.about" default="About"/></label>
        <trix:editor name="about" value="${recordCollectionEntity.about}"/>
    </div>

    <div class="form-group">
        <input type="submit" class="btn btn-primary" value="${message(code: 'recordCollection.update.submit', default: 'Update')}"/>
    </div>
</g:form>
</article>
<asset:stylesheet src="trix.css"/>
<asset:javascript src="trix.js"/>
</body>
</html>