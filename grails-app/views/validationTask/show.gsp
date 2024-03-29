<%@ page import="uk.co.metadataconsulting.monitor.RecordCorrectnessDropdown" %>
<%@ page import="uk.co.metadataconsulting.monitor.export.ExportFormat" %>

<html>
<head>
    <title>Validation Task</title>
    <meta name="layout" content="main" />
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-light bg-light justify-content-between">
    <g:render template="/templates/navbarBrand"/>
    <g:render template="/templates/logout"/>
</nav>


<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><g:link controller="validationTask" action="index"><g:message code="nav.validationTask.index" default="Validation Tasks"/></g:link></li>
        <li class="breadcrumb-item active" aria-current="page">${validationTask.name}</li>
    </ol>
</nav>

<g:render template="/templates/flashmessage"/>
<g:render template="/templates/flasherror"/>

<div class="jumbotron">
    <div class="float-right" style="padding: 10px;">

        <g:form ontroller="validationTask" action="importCsv" method="GET">
            <g:hiddenField name="validationTaskId" value="${validationTask.id}"/>
            <input type="submit" class="btn btn-success" value="${g.message(code: 'validationTask.importCsv.newValidationPass', default: 'Import CSV for new Validation Pass')}"/>
        </g:form>

    </div>
<g:if test="${validationTask}">
    <h3>${validationTask.name}</h3>
</g:if>

</div>

<article>


    <table class="table table-striped">
        <thead class="thead-dark">
        <tr>
            <th>
                Validation Pass
            </th>
            <th>
                Record Collection
            </th>
            <th>
                Date Created
            </th>
        </tr>
        </thead>
        <tbody>
        <g:if test="${validationPassList}">
            <g:each var="validationPass" in="${validationPassList}" status="i">
                %{--<g:if test="${validationPass.valid}">--}%
                    %{--<tr>--}%
                %{--</g:if>--}%
                %{--<g:else>--}%
                    %{--<tr class="alert-danger">--}%
                %{--</g:else>--}%
                <tr>
                    <td>
                        ${i + 1}
                    </td>
                    <td>
                        <g:link controller="record" action="index" params="[recordCollectionId: validationPass.recordCollection.id]">
                            ${validationPass.recordCollection.datasetName}
                        </g:link>
                    </td>
                    <td>
                        ${validationPass.dateCreated}
                    </td>
                </tr>
            </g:each>
        </g:if>
        </tbody>
    </table>
    <g:if test="${validationPassTotal > paginationQuery?.max}">
        <div class="pagination">
            <g:paginate controller="validationTask"
                        action="show"
                        total="${validationPassTotal}"
                        max="${paginationQuery?.max}"
                        offset="${paginationQuery?.offset}"
                        params="[
                                 validationTaskId: validationTask.id]" />
        </div>
        %{--<g:render template="/record/paginationinfo"/>--}%
    </g:if>

    <hr/>
    <p>
        <span id="dateCreated"> Created: <g:formatDate date="${validationTask.dateCreated}" type="datetime" style="LONG" timeStyle="SHORT"/>,</span>
        <span id="lastUpdated"> Updated: <g:formatDate date="${validationTask.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/></span>
    </p>

</article>
</body>
</html>
