<html>
<head>
    <title><g:message code="recordCollection.mapping" default="Record Collection Mapping"/></title>
    <meta name="layout" content="main" />
</head>
<body>
<nav class="navbar navbar-light bg-light">
    <g:render template="/templates/navbarBrand"/>
</nav>

<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><g:link controller="recordCollection" action="index" ><g:message code="nav.home" default="Home"/></g:link></li>
        <li class="breadcrumb-item"><g:link controller="record" action="index" params="[recordCollectionId: recordCollectionId]"><g:message code="nav.recordCollection" default="Record Collection"/></g:link></li>
        <li class="breadcrumb-item active" aria-current="page"><g:message code="recordCollection.mapping" default="Record Collection Mapping"/></li>
    </ol>
</nav>

<g:render template="/templates/flashmessage"/>
<g:render template="/templates/flasherror"/>

<g:if test="${recordPortionMappingList}">
    <g:form controller="recordCollectionMapping" action="update" method="POST">
        <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
    <table class="table table-striped">
        <thead class="thead-dark">
        <tr>
            <th><g:message code="recordPortionMapping.header" default="Header"/></th>
            <th><g:message code="recordPortionMapping.dataModel" default="Data Model"/></th>
            <th><g:message code="recordPortionMapping.catalogueElement" default="Catalogue Element"/></th>
        </tr>
        </thead>
        <tbody>
        <g:each var="recordPortionMapping" in="${recordPortionMappingList}">
            <td>${recordPortionMapping.header}</td>
            <td>
                <g:select noSelection="${['null':'Select One...']}"
                        optionKey="id"
                        optionValue="name"
                        name="dataModel${recordPortionMapping.id}"
                        from="${dataModelList}"
                        value="${dataModel}"
                        onChange="onDataModelChanged(${recordPortionMapping.id});"/>
            </td>
            <td><g:select
                    name="catalogueElement${recordPortionMapping.id}"
                    noSelection="${['null':'Select One...']}"
                    from="${[]}"/>
            </td>
            </tr>
        </g:each>
        </tbody>
    </table>
        <input type="submit" class="btn-primary btn" value="${g.message(code: 'recordCollection.mapping.save', default: 'Save')}"/>
    </g:form>
</g:if>
<g:javascript>
var getJSON = function(url, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.responseType = 'json';
    xhr.onload = function() {
        var status = xhr.status;
        if (status == 200) {
            callback(null, xhr.response);
        } else {
            callback(status);
        }
    };
    xhr.send();
};
function getSelectValue(selectId) {
    return document.getElementById(selectId).value;
}
function replaceWith(id, html) {
   document.getElementById(id).innerHTML = html;
}
function onDataModelChanged(recordPortionMappingId) {
    var dataModelId = getSelectValue('dataModel'+recordPortionMappingId)
    console.log('fetching catalogue elements of ' + dataModelId);
    getJSON('/recordCollectionMapping/catalogueElements/'+dataModelId, function(err, data) {
        if (err != null) {
            console.log('Something went wrong');

        } else {
            if ( data.length == 0 ) {
                console.log('No data returned');

            } else {
                var html = generateHtmlOptions(data, null);
                console.log(html);
                replaceWith('catalogueElement'+recordPortionMappingId, html)
            }
        }
    });
}
function generateHtmlOptions(idNameValues, id) {
    var html = '';
    for (var i = 0; i < idNameValues.length; i++ ) {
        var idName = idNameValues[i];
        if ( idName.id === id ) {
            html += '<option value="' + idName.id + '" selected="selected">' + idName.name.substring(0, 30) +'</option>'
        } else {
            html += '<option value="' + idName.id + '">'+idName.name.substring(0, 30)+'</option>'
        }
    }
    return html;
}
</g:javascript>
</body>
</html>