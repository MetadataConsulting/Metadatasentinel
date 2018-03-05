<%@ page import="uk.co.metadataconsulting.sentinel.RecordCorrectnessDropdown" %>
<html>
<head>
    <title>Records</title>
    <meta name="layout" content="main" />
</head>
<body>
<div id="content" role="main">
    <section class="row colset-2-its">
        <article>
            <g:render template="/templates/flashmessage"/>
        <g:render template="/templates/flasherror"/>

            <g:form controller="recordCollection" action="validate" method="POST">
                <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
                <input type="submit" class="btn-primary btn" value="${g.message(code: 'record.validate', default: 'Validate')}"/>
            </g:form>

        <g:form controller="record" action="index" method="GET">
            <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
            <g:select name="correctness" from="${RecordCorrectnessDropdown.values()}" value="${correctness}"/>
            <input type="submit" value="${g.message(code: 'record.filter', default: 'Filter')}"/>
        </g:form>
        <g:if test="${recordList}">
            <g:render template="/record/paginationinfo" model="[
                    correctness: correctness,
                    recordTotal: recordTotal,
                    invalidRecordTotal: invalidRecordTotal,
                    allRecordTotal: allRecordTotal]"
            />
            <table>
                <thead>
                <tr>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <g:each var="record" in="${recordList}" status="i">
                    <g:if test="${record.valid}">
                        <tr>
                    </g:if>
                    <g:else>
                        <tr class="alert-danger">
                    </g:else>
                        <td>
                            <g:link controller="record" action="show" params="[recordCollectionId: recordCollectionId, recordId: record.id]">
                                <g:message code="record.row" args="${(paginationQuery?.offset ?: 0) + i + 1}" default="Row {0}"/>
                            </g:link>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>

            <div class="pagination">
                <g:paginate controller="record"
                            action="index"
                            total="${recordTotal}"
                            max="${paginationQuery?.max}"
                            offset="${paginationQuery?.offset}"
                            params="[correctness: correctness, recordCollectionId: recordCollectionId]" />
            </div>
        </g:if>
            <g:render template="/record/paginationinfo"/>
        </article>
    </section>
</div><!-- #content -->
</body>
</html>