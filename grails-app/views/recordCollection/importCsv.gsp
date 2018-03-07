<%@ page import="uk.co.metadataconsulting.sentinel.RecordFileCommand" %>
<html>
<head>
    <title><g:message code="recordCollection.import" default="Import File"/></title>
    <meta name="layout" content="main" />
    <style type="text/css">
    form fieldset ol li { list-style-type: none; }
    </style>
</head>
<body>
<nav class="navbar navbar-light bg-light">
    <g:render template="/templates/navbarBrand"/>
</nav>
<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><g:link controller="recordCollection" action="index"><g:message code="nav.home" default="Home"/></g:link></li>
        <li class="breadcrumb-item active" aria-current="page"><g:message code="nav.importFile" default="Import File"/></li>
    </ol>
</nav>
<div class="ml-5">
    <h1><g:message code="recordCollection.import" default="Import File" /></h1>
    <g:render template="/templates/flashmessage"/>
    <g:render template="/templates/flasherror"/>
    <g:uploadForm action="uploadCsv" controller="recordCollection">
        <div class="form-group">
            <label for="csvFile"><g:message code="recordCollection.import.file" args="${[RecordFileCommand.allowedExtensions().join(',')]}" default="File ({0})"/></label>
            <input type="file" name="csvFile" />
        </div>
        <div class="form-group">
            <label for="mapping"><g:message code="recordCollection.csv.mapping" default="Mapping"/></label>
            <textarea name="mapping"></textarea>
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
            <input type="submit" class="btn btn-primary" value="${message(code: 'recordCollection.import.submit', default: 'Submit')}"/>
        </div>
    </g:uploadForm>
</div>

</body>
</html>