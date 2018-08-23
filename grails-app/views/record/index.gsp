<%@ page import="uk.co.metadataconsulting.sentinel.RecordCorrectnessDropdown" %>
<%@ page import="uk.co.metadataconsulting.sentinel.export.ExportFormat" %>

<html>
<head>
    <title>Records</title>
    <meta name="layout" content="main" />
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-light bg-light sticky-top">
    <g:render template="/templates/navbarBrand"/>
    <g:render template="/templates/logout"/>
    <div class="collapse navbar-collapse  justify-content-end" id="navbarSupportedContent">
    <ul class="navbar-nav mr-auto">

        <li class="nav-item">
            <g:form controller="recordCollection" action="edit" method="GET" class="form-inline my-2 my-lg-0">
                <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
                <input type="submit" class="btn btn-default my-2 my-sm-0" value="${g.message(code: 'recordCollection.edit', default: 'Edit')}"/>
            </g:form>
        </li>

        <li class="nav-item">
            <g:form controller="recordCollection" action="validate" method="POST" class="form-inline my-2 my-lg-0">
                <g:hiddenField name="recordId" value="${recordId}"/>
                <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
                <g:hiddenField name="datasetName" value="${datasetName}"/>
                <input type="submit" class="btn btn-outline-success my-2 my-sm-0" value="${g.message(code: 'record.validate', default: 'Validate')}"/>
            </g:form>
        </li>

        <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="MappingDrop" role="button" data-toggle="dropdown">
                Mapping
            </a>
            <div class="dropdown-menu">
                <a class="dropdown-item" >
                    <g:link class="nav-link" controller="recordCollection" action="headersMapping" params="[recordCollectionId: recordCollectionId]">
                        <g:message code="recordCollection.headersMapping" default="Edit Mapping"/>
                    </g:link>
                </a>
                <a class="dropdown-item" >
                    <g:link class="nav-link" controller="recordCollection" action="cloneMapping" params="[recordCollectionId: recordCollectionId]">
                        <g:message code="recordCollection.mapping.clone" default="Clone Mapping"/>
                    </g:link>
                </a>
            </div>
        </li>

        <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="export" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Export
            </a>
            <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                <g:each in="${ExportFormat.values()}">
                    <g:link class="dropdown-item"  controller="recordCollection" action="export" method="GET" params='[recordCollectionId: "${recordCollectionId}", format: "${it}"]'>${it}</g:link>
                </g:each>
            </div>
        </li>

        <li class="nav-item">
            <g:form controller="recordCollection" action="delete">
                <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
                <input onclick="return confirm('${g.message(code: "recordCollection.delete.confirmation",
                                                            default: "Are you sure you want to delete this record collection?")}');"
                       type="submit"
                       class="btn btn-danger"
                       value="${g.message(code: 'recordCollection.delete', default: 'Delete')}"/>
            </g:form>
        </li>



    </ul>


</div>
</nav>


<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><g:link controller="recordCollection" action="index"><g:message code="nav.home" default="Home"/></g:link></li>
        <li class="breadcrumb-item active" aria-current="page">${recordCollectionEntity.datasetName}</li>
    </ol>
</nav>

<g:render template="/templates/flashmessage"/>
<g:render template="/templates/flasherror"/>

<article>
    <g:if test="${recordCollectionEntity}">
        <h1 class="center">Record Collection: ${recordCollectionEntity.datasetName}</h1>
        <g:if test="${recordCollectionEntity.fileUrl}">
            <h3><a href="${recordCollectionEntity.fileUrl}">s3 file</a></h3>
        </g:if>
        ${recordCollectionEntity.about.encodeAsRaw()}
    </g:if>
    <hr/>

    <table class="table table-striped">
        <thead class="thead-dark">
        <tr>
            <th>
                <div class="float-right">
                    <a class="nav-link dropdown-toggle" href="#" id="filter" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        Filter
                    </a>
                    <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                        <g:each in="${RecordCorrectnessDropdown.values()}">
                            <g:link class="dropdown-item"  action="index" params='[recordCollectionId: "${recordCollectionId}", correctness: "${it}"]'>${it}</g:link>
                        </g:each>
                    </div>
                </div>
                <g:message code="record.th." default="Record"/>
            </th>
        </tr>
        </thead>
        <tbody>
        <g:if test="${recordList}">
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
        </g:if>
        </tbody>
    </table>
    <g:if test="${recordTotal > paginationQuery?.max}">
        <div class="pagination">
            <g:paginate controller="record"
                        action="index"
                        total="${recordTotal}"
                        max="${paginationQuery?.max}"
                        offset="${paginationQuery?.offset}"
                        params="[correctness: correctness,
                                 recordCollectionId: recordCollectionId]" />
        </div>
        <g:render template="/record/paginationinfo"/>
    </g:if>

    <hr/>
    <p><g:message code="record.createdBy" default="Created by"/> <b><span id="createdBy">${recordCollectionEntity.createdBy}</span></b>
        <span id="dateCreated"><g:formatDate date="${recordCollectionEntity.dateCreated}" type="datetime" style="LONG" timeStyle="SHORT"/></span>
    </p>
    <p><g:message code="record.updatedBy" default="Updated by"/>
        <b><span id="updatedBy">${recordCollectionEntity.updatedBy}</span></b>
        <span id="lastUpdated"><g:formatDate date="${recordCollectionEntity.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/></span>
    </p>
</article>
</body>
</html>