<html>
<head>
    <title>Record Collections</title>
    <meta name="layout" content="main" />
</head>
<body>
<div id="content" role="main">
    <section class="row colset-2-its">
        <article>
            <g:render template="/templates/flashmessage"/>
            <g:render template="/templates/flasherror"/>

            <g:if test="${recordCollectionList}">
            <table>
                <thead>
                <tr>
                    <th></th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <g:each var="recordCollection" in="${recordCollectionList}">
                    <tr>
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

            <div class="pagination">
            <g:paginate controller="recordCollection" action="index" total="${recordCollectionTotal}" max="${paginationQuery?.max}" offset="${paginationQuery?.offset}" />
            </div>
        </g:if>


        <g:link controller="recordCollection" action="importCsv" class="btn-primary btn">
            <g:message code="recordCollection.import" default="Import File"/>
        </g:link>

        </article>
    </section>
</div><!-- #content -->
</body>
</html>