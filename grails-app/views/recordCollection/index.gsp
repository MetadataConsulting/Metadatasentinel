<html>
<head>
    <title>Record Collections</title>
    <meta name="layout" content="main" />
</head>
<body>
<div id="content" role="main">
    <article>
        <g:render template="/templates/flashmessage"/>
        <g:render template="/templates/flasherror"/>

        <g:if test="${recordCollectionList}">
            <table>
                <thead>
                <tr>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <g:each var="recordCollection" in="${recordCollectionList}">
                    <tr>
                        <td><g:link controller="record" action="index" params="[recordCollectionId: recordCollection?.id]">${recordCollection.lastUpdated}</g:link></td>
                    </tr>
                </g:each>
                </tbody>
            </table>

            <g:paginate controller="recordCollection" action="index" total="${recordCollectionListTotal}" max="${paginationQuery?.max}" offset="${paginationQuery?.offset}" />

        </g:if>


        <g:link controller="recordCollection" action="importCsv" class="btn-primary btn">
            <g:message code="recordCollection.csv.import" default="Import CSV"/>
        </g:link>
    </article>
</div><!-- #content -->
</body>
</html>