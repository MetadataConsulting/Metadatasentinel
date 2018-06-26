<html>
<head>
    <title>Record Collections</title>
    <meta name="layout" content="main" />
</head>
<body>
<nav class="navbar navbar-light bg-light">
    <g:render template="/templates/navbarBrand"/>
    <g:link id="importfile-link" controller="recordCollection" action="importCsv" class="btn btn-primary">
        <g:message code="recordCollection.import" default="Import File"/>
    </g:link>
</nav>
<g:render template="/templates/flashmessage"/>
<g:render template="/templates/flasherror"/>

<g:if test="${recordCollectionList}">
    <article>
        <table class="table table-striped">
            <thead class="thead-dark">
            <tr>
                <th><g:message code="recordCollection.datasetname" default="Dataset name"/></th>
                <th><g:message code="recordCollection.th.lastUpdated" default="Record Collection Creation Date"/></th>
                <th><g:message code="recordCollection.th.actions" default="Actions"/></th></th>
            </tr>
            </thead>
            <tbody>
            <g:each var="recordCollection" in="${recordCollectionList}">
                <tr>
                    <td><g:link controller="record" action="index" params="[recordCollectionId: recordCollection?.id]">${recordCollection.datasetName}</g:link></td>
                    <td><g:link controller="record" action="index" params="[recordCollectionId: recordCollection?.id]">${recordCollection.lastUpdated}</g:link></td>
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