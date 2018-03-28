<html>
<head>
    <title><g:message code="recordCollection.mapping" default="Record Collection Mapping"/></title>
    <meta name="layout" content="main" />
</head>
<body>
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
        var element = document.getElementById(selectId)
        if ( element != null ) {
            return element.value;
        }
        console.log('not found value for' + selectId);
        return null
    }
    function replaceWith(id, html) {
        document.getElementById(id).innerHTML = html;
    }
    function onDataModelChanged(recordPortionMappingId) {
        var dataModelId = getSelectValue('dataModel'+recordPortionMappingId);
        var targetId = 'catalogueElement'+recordPortionMappingId;
        if ( dataModelId == 'null') {
            replaceWith(targetId, selectOneOption());

        } else {
            console.log('fetching catalogue elements of ' + dataModelId);
            getJSON('/recordCollectionMapping/catalogueElements/'+dataModelId, function(err, data) {

                if (err != null) {
                    console.log('Something went wrong');

                } else {
                    if ( data.length == 0 ) {
                        console.log('No data returned');
                        replaceWith(targetId, selectOneOption());

                    } else {
                        var gormUrl = document.getElementById('gormUrl'+recordPortionMappingId).getAttribute('value');
                        var html = generateHtmlOptions(data, gormUrl);
                        console.log(html);
                        replaceWith(targetId, html);
                    }
                }
            });
        }
    }
    function onCatalogueElementChanged(recordPortionMappingId) {
        var value = getSelectValue('catalogueElement'+recordPortionMappingId);
        if ( value != null) {
            var element = document.getElementById('gormUrl' + recordPortionMappingId)
            if (element != null && element.hasAttribute('value') ) {
                element.setAttribute('value', value);
            }
        }
    }
    function selectOneOption() {
        return '<option value="null">Select One...</option>';
    }
    function generateHtmlOptions(gormUrlNameValues, gormUrl) {
        var html = selectOneOption();
        for (var i = 0; i < gormUrlNameValues.length; i++ ) {
            var idName = gormUrlNameValues[i];
            if ( idName.gormUrl === gormUrl) {
                html += '<option value="' + idName.gormUrl + '" selected="selected">' + idName.name.substring(0, 30) +'</option>'
            } else {
                html += '<option value="' + idName.gormUrl + '">'+idName.name.substring(0, 30)+'</option>'
            }
        }
        return html;
    }
</g:javascript>
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

<g:if test="${dataModelList}">
    <article>
    <g:if test="${recordPortionMappingList}">
        <g:form controller="recordCollectionMapping" action="update" method="POST">
            <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
            <table class="table table-striped">
                <thead class="thead-dark">
                <tr>
                    <th><g:message code="recordPortionMapping.header" default="Header"/></th>
                    <th><g:message code="recordPortionMapping.dataModel" default="Data Model"/></th>
                    <th><g:message code="recordPortionMapping.catalogueElement" default="Catalogue Element"/></th>
                    <th><g:message code="recordPortionMapping.gormUrl" default="GORM Url"/></th>
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
                                  value="${recordPortionMapping.dataModelId}"
                                  onChange="onDataModelChanged(${recordPortionMapping.id});"/>
                        <g:if test="${recordPortionMapping.dataModelId}">
                            <g:javascript>
                        onDataModelChanged(${recordPortionMapping.id});
                            </g:javascript>
                        </g:if>

                    </td>
                    <td><g:select
                            name="catalogueElement${recordPortionMapping.id}"
                            noSelection="${['null':'Select One...']}"
                            from="${[]}"
                            onChange="onCatalogueElementChanged(${recordPortionMapping.id});"/>
                    </td>
                    <td>
                        <input type="text" disabled="disabled" id="gormUrl${recordPortionMapping.id}" name="gormUrl${recordPortionMapping.id}" value="${recordPortionMapping.gormUrl}" />
                    </td>
                    </tr>
                </g:each>
                </tbody>
            </table>
            <input type="submit" class="btn-primary btn" value="${g.message(code: 'recordCollection.mapping.save', default: 'Save')}"/>
        </g:form>
    </g:if>
    <article>
</g:if>
</body>
</html>