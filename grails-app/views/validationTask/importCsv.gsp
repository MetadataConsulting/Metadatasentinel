<%@ page import="uk.co.metadataconsulting.monitor.validationTask.ValidationTaskFileCommand" %>
<!-- Copied from recordCollection/importCsv.gsp-->
<html>
<head>
    <title><g:message code="validationTask.csv.import.title" default="Import CSV File to Create New Validation Task"/></title>
    <meta name="layout" content="main" />
    <style type="text/css">
    form fieldset ol li { list-style-type: none; }
    </style>

    <asset:javascript src="bootstrap-notify.js"/>
    <asset:javascript src="selectize.js"/>
    <asset:stylesheet src="selectize.css"/>
    <asset:javascript src="jquery.collapse.js"/>
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
        <g:if test="${validationTask}">
            <h1 class="center">Import CSV File to Create New Validation Pass for Validation Task <i>"${validationTask.name}"</i></h1>
        </g:if>
        <g:else>
            <h1 class="center"><g:message code="validationTask.csv.import.title" default="Import CSV File to Create New Validation Task"/></h1>
        </g:else>

        <br/>
        <g:render template="/templates/flashmessage"/>
        <g:render template="/templates/flasherror"/>
        <g:uploadForm action="uploadCsv" controller="validationTask">
            <g:hiddenField name="validationTaskId" value="${validationTask?.id}"/>
            <ol>
                <li>
                    <div class="form-group">
                        <label for="csvFile"><g:message code="validationTask.import.file" args="${[ValidationTaskFileCommand.allowedExtensions().join(', ')]}" default="Upload a file (allowed formats: {0})"/></label>
                        <input type="file" class="btn btn-default" name="csvFile" />
                    </div>
                </li>
                <li>
                    <div class="form-group">
                        <label for="datasetName"><g:message code="validationTask.csv.import.datasetName" default="Dataset Name"/>
                            <g:textField name="datasetName" value="${datasetName}" id="datasetName" /></label>
                    </div>
                </li>
                <li>
                    <div class="form-group">
                        <g:select noSelection="${['':'Select A Data Model...']}"
                                  optionKey="id"
                                  optionValue="name"
                                  name="dataModelId"
                                  from="${dataModelList}"
                                  value="${dataModelId}"
                        />
                    </div>
                    <g:javascript>
                        $('#dataModelId').selectize()
                    </g:javascript>

                    <div id="data-model-explanation">
                        <i class="collapse-heading">What is a Data Model?</i>
                        <p>A DataModel contains the Elements which have rules against which to validate your data.</p>
                    </div><br/>

                    <g:javascript>
                        $("#data-model-explanation").collapse({
                            query: '.collapse-heading'
                        })
                    </g:javascript>
                </li>
                <li>
                    <div class="form-group">
                        <label for="batchSize"><g:message code="validationTask.csv.import.batchSize" default="Batch Size"/></label>
                        <select name="batchSize">
                            <option value="10">10</option>
                            <option value="100">100</option>
                            <option value="200">200</option>
                            <option value="500">500</option>
                            <option value="1000">1000</option>
                        </select>
                    </div>
                </li>
                <li>
                    <div class="form-group">
                        <label for="about"><g:message code="validationTask.about" default="About (Describe this Dataset)"/></label>
                        <trix:editor name="about" id="about" value="${about}"/>
                    </div>
                </li>

            </ol>

            <div class="form-group">
                <input type="submit" class="btn btn-primary" value="${message(code: 'validationTask.import.submit', default: 'Submit')}"/>
            </div>
        </g:uploadForm>
    </div>
</article>
<asset:stylesheet src="trix.css"/>
<asset:javascript src="trix.js"/>
</body>
</html>