<html>
<head>
    <title>Record Portions</title>
    <meta name="layout" content="main" />
</head>
<body>
<div id="content" role="main">
    <section class="row colset-2-its">
        <article>

            <g:render template="/templates/flashmessage"/>
            <g:render template="/templates/flasherror"/>

            <g:form controller="record" action="validate" method="POST">
                <g:hiddenField name="recordId" value="${recordId}"/>
                <input type="submit" class="btn-primary btn" value="${g.message(code: 'record.validate', default: 'Validate')}"/>
            </g:form>

        <g:if test="${recordPortionList}">
            <table>
                <thead>
                <tr>
                    <th><g:message code="recordPortion.header" default="Header"/></th>
                    <th><g:message code="recordPortion.name" default="Name"/></th>
                    <th></th>
                    <th><g:message code="recordPortion.value" default="Value"/></th>
                    <th><g:message code="recordPortion.numberOfRulesValidatedAgainst" default="# Rules"/></th>
                    <th><g:message code="recordPortion.valid" default="Valid"/></th>
                    <th><g:message code="recordPortion.failure.reason" default="Failure reason"/></th></th>
                    <th><g:message code="recordPortion.lastUpdated" default="Last Updated"/></th>
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
                        <td>${recordPortion.header}</td>
                        <td>${recordPortion.name}</td>
                        <td><small>${recordPortion.gormUrl}</small></td>
                        <td>${recordPortion.value}</td>
                        <td>${recordPortion.numberOfRulesValidatedAgainst}</td>
                        <td>${recordPortion.valid}</td>
                        <td>${recordPortion.reason}</td>
                        <td>${recordPortion.lastUpdated}</td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </g:if>
        </article>
    </section>
</div><!-- #content -->
</body>
</html>