<html>
<head>
    <title>Validation Tasks</title>
    <meta name="layout" content="main" />
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light sticky-top">
    <g:render template="/templates/navbarBrand"/>
    <g:render template="/templates/logout"/>
    <div class="row justify-content-end">
    </div>

</nav>
<g:render template="/templates/flashmessage"/>
<g:render template="/templates/flasherror"/>
<center><h1>Validation Tasks</h1>
    <div style="padding: 10px;">
        <g:link id="importfile-link" controller="validationTask" action="importCsv" class="btn btn-primary">
            <g:message code="validationTask.start" default="Start New Validation Task"/>
        </g:link>
    </div></center>


<g:if test="${validationTaskList}">
    <article>
        <table class="table table-striped border-collapse">
            <thead class="thead-light">
            <tr>
                <th><g:message code="validationTask.name" default="Validation Task name"/></th>
                <th><g:message code="validationTask.numValidationPasses" default="Number of Validation Passes"/></th>
                <th class="right-border"></th> %{-- Import CSV button --}%
                <th><g:message code="validationTask.latestValidationPassRecordCollection.datasetname" default="Dataset name of last Validation Pass"/></th>
                <th><g:message code="validationTask.latestValidationPassRecordCollection.dataModelName" default="Data Model name"/></th>
                <th><g:message code="validationTask.latestValidationPassRecordCollection.th.createdBy" default="Created By"/></th>
                <th><g:message code="validationTask.latestValidationPassRecordCollection.th.dateCreated" default="Date Created"/></th>
                <th><g:message code="validationTask.latestValidationPassRecordCollection.th.updatedBy" default="Updated By"/></th>
                <th><g:message code="validationTask.latestValidationPassRecordCollection.th.lastUpdated" default="Last Updated"/></th>
                <th></th> %{-- Edit button --}%
                <th></th> %{-- Delete button --}%
            </tr>
            </thead>
            <tbody>
            <g:set var="validationTaskGormService" bean="validationTaskGormService"/>
            <g:each var="validationTask" in="${validationTaskList}">
                <tr>
                    <td>${validationTask.name}</td>
                    %{--Eventually we might want to use service methods to call more precise database routines, otherwise we'll have hasMany problems.--}%
                    <td>${validationTaskGormService.countValidationPasses(validationTask.id)}</td>
                    <td class="right-border">
                        <g:form controller="validationTask" action="importCsv" method="GET">
                            <g:hiddenField name="validationTaskId" value="${validationTask.id}"/>
                            <input type="submit" class="btn btn-success" value="${g.message(code: 'validationTask.importCsv.newValidationPass', default: 'Import CSV for new Validation Pass')}"/>
                        </g:form>
                    </td>
                    <g:if test="${validationTask.validationPasses.size() > 0}">
                    <g:set var="recordCollection" value="${validationTask.validationPasses[-1].recordCollection}"/>
                    <td><g:link controller="record" action="index" params="[recordCollectionId: recordCollection?.id]">${recordCollection.datasetName}</g:link></td>
                    <td>${recordCollection.dataModelName}</td>
                    <td>${recordCollection.createdBy}</td>
                    <td>
                        <g:formatDate date="${recordCollection.dateCreated}" type="datetime" style="LONG" timeStyle="SHORT"/>
                    </td>
                    <td>${recordCollection.updatedBy}</td>
                    <td>
                        <g:formatDate date="${recordCollection.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/>
                    </td>
                    <td>
                        <g:form controller="recordCollection" action="edit" method="GET">
                            <g:hiddenField name="recordCollectionId" value="${recordCollection?.id}"/>
                            <input type="submit" class="btn btn-default" value="${g.message(code: 'recordCollection.edit', default: 'Edit')}"/>
                        </g:form>
                    </td>
                    <td>

                        <g:form controller="recordCollection" action="delete">
                            <g:hiddenField name="recordCollectionId" value="${recordCollection?.id}"/>
                            <input onclick="return confirm('${g.message(code: "recordCollection.delete.confirmation",
                                                                    default: "Are you sure you want to delete this record collection?")}');"
                                   type="submit"
                                   class="btn btn-danger"
                                   value="${g.message(code: 'recordCollection.delete', default: 'Delete')}"/>
                        </g:form>
                    </td>
                    </g:if>

                </tr>
            </g:each>
            </tbody>
        </table>
        <g:if test="${validationTaskTotal > paginationQuery?.max}">
            <div class="pagination">
                <g:paginate controller="validationTask" action="index" total="${validationTaskTotal}" max="${paginationQuery?.max}" offset="${paginationQuery?.offset}" />
            </div>
        </g:if>
    </article>
</g:if>

</body>
</html>