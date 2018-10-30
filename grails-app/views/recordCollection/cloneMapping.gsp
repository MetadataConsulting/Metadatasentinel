<html>
<head>
    <title><g:message code="recordCollection.mapping.clone" default="Clone Mapping"/></title>
    <meta name="layout" content="main" />
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light justify-content-between">
    <g:render template="/templates/navbarBrand"/>
    <g:render template="/templates/logout"/>
</nav>

<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><g:link controller="recordCollection" action="index" ><g:message code="nav.home" default="Home"/></g:link></li>
        <li class="breadcrumb-item"><g:link controller="record" action="index" params="[recordCollectionId: toRecordCollectionId]">${recordCollectionEntity.datasetName}</g:link></li>
        <li class="breadcrumb-item active" aria-current="page"><g:message code="recordCollection.mapping.clone" default="Clone Mapping"/></li>
    </ol>
</nav>

<g:render template="/templates/flashmessage"/>
<g:render template="/templates/flasherror"/>

<article>
    <div class="ml-5">
       <g:uploadForm action="cloneSave" controller="recordCollection">
        <g:hiddenField name="toRecordCollectionId" value="${toRecordCollectionId}"/>
        <div class="form-group">
            <label for="fromRecordCollectionId"><g:message code="recordCollection.mapping.clone.from" default="From Record Collection"/></label>
            <g:select from="${recordCollectionList.findAll { it.id != toRecordCollectionId}}" name="fromRecordCollectionId" optionValue="lastUpdated" optionKey="id"/>
        </div>
        <div class="form-group">
            <input type="submit" class="btn btn-primary" value="${message(code: 'recordCollection.mapping.clone.submit', default: 'Clone')}"/>
        </div>
    </g:uploadForm>
    </div>
</article>
</body>
</html>
