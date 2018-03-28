<html>
<head>
    <title>Record Portions</title>
    <meta name="layout" content="main" />
</head>
<body>
<nav class="navbar navbar-light bg-light">
    <g:render template="/templates/navbarBrand"/>
    <g:form controller="record" action="validate" method="POST">
        <g:hiddenField name="recordId" value="${recordId}"/>
        <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
        <input type="submit" class="btn-primary btn" value="${g.message(code: 'record.validate', default: 'Validate')}"/>
    </g:form>
</nav>

<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><g:link controller="recordCollection" action="index"><g:message code="nav.home" default="Home"/></g:link></li>
        <li class="breadcrumb-item"><g:link controller="record" action="index" params="[recordCollectionId: recordCollectionId]"><g:message code="nav.recordCollection" default="Record Collection"/></g:link></li>
        <li class="breadcrumb-item active" aria-current="page"><g:message code="nav.record" default="Record"/></li>
    </ol>
</nav>

<g:render template="/templates/flashmessage"/>
<g:render template="/templates/flasherror"/>

<g:if test="${recordPortionList}">
    <table class="table table-striped">
        <thead class="thead-dark">
        <tr>
            <th><g:message code="recordPortion.header" default="Header"/></th>
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
            <td>
                <g:if test="${recordPortion.header}">
                    <mdx:link recordPortionMappingList="${recordPortionMappingList}" recordPortion="${recordPortion}"/>
                </g:if>
            </td>
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
</body>
</html>