<%@ page import="uk.co.metadataconsulting.sentinel.RecordCorrectnessDropdown" %>
<html>
<head>
    <title>Records</title>
    <meta name="layout" content="main" />
</head>
<body>
<div id="content" role="main">
    <article>
        <g:render template="/templates/flashmessage"/>
        <g:render template="/templates/flasherror"/>

        <g:form controller="record" action="index" method="GET">
            <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
            <g:select name="correctness" from="${RecordCorrectnessDropdown.values()}" value="${correctness}"/>
            <input type="submit" value="${g.message(code: 'record.filter', default: 'Filter')}"/>
        </g:form>

        <g:if test="${recordList}">
            <table>
                <thead>
                <tr>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <g:each var="record" in="${recordList}">
                    <tr>
                        <td><g:link controller="recordPortion" action="show" params="[recordCollectionId: recordCollectionId, recordId: record.id]">${record.id}</g:link></td>
                    </tr>
                </g:each>
                </tbody>
            </table>

            <g:paginate controller="record"
                        action="index"
                        total="${recordTotal}"
                        max="${paginationQuery?.max}"
                        offset="${paginationQuery?.offset}"
                        params="[recordCollectionId: recordCollectionId]" />

        </g:if>
        <p><span><g:message code="record.total" default="Number of records"/> ${recordTotal}</span></p>
    </article>
</div><!-- #content -->
</body>
</html>