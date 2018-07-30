<html>
<head>
    <title>Record Collections</title>
    <meta name="layout" content="main" />
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light sticky-top">
    <g:render template="/templates/navbarBrand"/>
    <g:render template="/templates/logout"/>
    <div class="row justify-content-end">
        <div style="padding: 10px;">
            <g:link id="importfile-link" controller="recordCollection" action="importCsv" class="btn btn-primary">
                <g:message code="recordCollection.import" default="Import File"/>
            </g:link>
        </div>
    </div>

</nav>
<g:render template="/templates/flashmessage"/>
<g:render template="/templates/flasherror"/>

<g:if test="${recordCollectionList}">
    <article>
        <table class="table table-striped">
            <thead class="thead-dark">
            <tr>
                <th><g:message code="recordCollection.datasetname" default="Dataset name"/></th>
                <th><g:message code="recordCollection.dataModelName" default="Data Model name"/></th>
                <th><g:message code="recordCollection.th.createdBy" default="Created By"/></th>
                <th><g:message code="recordCollection.th.dateCreated" default="Date Created"/></th>
                <th><g:message code="recordCollection.th.updatedBy" default="Updated By"/></th>
                <th><g:message code="recordCollection.th.lastUpdated" default="Last Updated"/></th>
                <th></th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <g:each var="recordCollection" in="${recordCollectionList}">
                <tr>
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
                            <input type="submit" class="btn btn-danger" value="${g.message(code: 'recordCollection.delete', default: 'Delete')}"/>
                        </g:form>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
        <g:if test="${recordCollectionTotal > paginationQuery?.max}">
            <div class="pagination">
                <g:paginate controller="recordCollection" action="index" total="${recordCollectionTotal}" max="${paginationQuery?.max}" offset="${paginationQuery?.offset}" />
            </div>
        </g:if>
    </article>
</g:if>

</body>
</html>