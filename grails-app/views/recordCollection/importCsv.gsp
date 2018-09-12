<%@ page import="uk.co.metadataconsulting.monitor.RecordFileCommand" %>
<html>
<head>
    <title><g:message code="recordCollection.import" default="Import File"/></title>
    <meta name="layout" content="main" />
    <style type="text/css">
    form fieldset ol li { list-style-type: none; }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light sticky-top">
    <g:render template="/templates/navbarBrand"/>
    <g:render template="/templates/logout"/>
</nav>
<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><g:link controller="recordCollection" action="index"><g:message code="nav.home" default="Home"/></g:link></li>
        <li class="breadcrumb-item active" aria-current="page"><g:message code="nav.importFile" default="Import File"/></li>
    </ol>
</nav>
<article>
<div class="ml-5">
    <h1><g:message code="recordCollection.import" default="Import File" /></h1>
    <g:render template="/templates/flashmessage"/>
    <g:render template="/templates/flasherror"/>
    <g:uploadForm action="uploadCsv" controller="recordCollection">
        <div class="form-group">
            <label for="datasetName"><g:message code="recordCollection.csv.import.datasetName" default="Dataset Name"/>
                <g:textField name="datasetName" value="${datasetName}" id="datasetName" /></label>
        </div>
        <div class="form-group">
            <g:select noSelection="${['null':'Select One...']}"
                      optionKey="id"
                      optionValue="name"
                      name="dataModelId"
                      from="${dataModelList}"
                      value="${dataModelId}"
                      />
        </div>
        <div class="form-group">
            <label for="csvFile"><g:message code="recordCollection.import.file" args="${[RecordFileCommand.allowedExtensions().join(',')]}" default="File ({0})"/></label>
            <input type="file" class="btn btn-default" name="csvFile" />
        </div>
        <div class="form-group">
            <label for="batchSize"><g:message code="recordCollection.csv.import.batchSize" default="Batch Size"/></label>
            <select name="batchSize">
                <option value="10">10</option>
                <option value="100">100</option>
                <option value="200">200</option>
                <option value="500">500</option>
                <option value="1000">1000</option>
            </select>
        </div>
        <div class="form-group">
            <label for="about"><g:message code="recordCollection.about" default="About"/></label>
            <trix:editor name="about" id="about" value="${about}"/>
        </div>
        <div class="form-group">
            <input type="submit" class="btn btn-primary" value="${message(code: 'recordCollection.import.submit', default: 'Submit')}"/>
        </div>
    </g:uploadForm>
</div>
</article>
<asset:stylesheet src="trix.css"/>
<asset:javascript src="trix.js"/>
</body>
</html>