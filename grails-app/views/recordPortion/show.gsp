<html>
<head>
    <title>Record Portions</title>
    <meta name="layout" content="main" />
</head>
<body>
<div id="content" role="main">
    <article>
        <g:render template="/templates/flashmessage"/>
        <g:render template="/templates/flasherror"/>

        <g:if test="${recordPortionList}">
            <table>
                <thead>
                <tr>
                    <th></th>
                    <th><g:message code="recordPortion.value" default="Value"/></th>
                    <th><g:message code="recordPortion.valid" default="Valid"/></th>
                    <th><g:message code="recordPortion.failure.reason" default="Failure reason"/></th></th>
                </tr>
                </thead>
                <tbody>
                <g:each var="recordPortion" in="${recordPortionList}">
                    <g:if test="${recordPortion.valid}">
                        <tr>
                    </g:if>
                    <g:else>
                        <tr class="alert-danger">
                    </g:else>
                        <td>${recordPortion.name}</td>
                        <td>${recordPortion.value}</td>
                        <td>${recordPortion.valid}</td>
                        <td>${recordPortion.reason}</td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </g:if>
    </article>
</div><!-- #content -->
</body>
</html>