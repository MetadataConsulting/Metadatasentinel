<html>
<head>
    <title><g:message code="recordCollection.mapping" default="Record Collection Mapping"/></title>
    <meta name="layout" content="main" />
    <g:if test="${!recordPortionMappingList}">
        <meta http-equiv="refresh" content="2" />
    </g:if>
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
    function getInputValue(inputId) {
        var element = document.getElementById(inputId)
        if ( element != null ) {
            return element.value;
        }
        console.log('not found value for' + inputId);
        return null
    }
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

    function onSaveMapping(recordPortionMappingId) {
        var targetId = 'catalogueElement'+recordPortionMappingId;
        var gormUrl = getSelectValue(targetId);
        var url = '/recordCollectionMapping/'+recordPortionMappingId+'/save?gormUrl='+gormUrl;
        getJSON(url, function(err, data) {
            if (err != null) {
                console.log('Something went wrong');
            } else {
                greenfade(document.getElementById(targetId).parentElement);
                selectOption(targetId, data.gormUrl)
            }
        });
    }
    function selectOption(selectId, optionValue) {
        for (var x = 0; x < document.getElementById(selectId).options.length; x++) {
            if(document.getElementById(selectId).options[x].getAttribute('value') === optionValue) {
                document.getElementById(selectId).options[x].setAttribute("selected", "selected");
            }
        }
    }
    function onQueryChanged(recordPortionMappingId) {
        var queryId = "catalogueElement"+recordPortionMappingId+"Query";
        var query = getInputValue(queryId);
        var dataModelId = getSelectValue('dataModelId');
        var targetId = 'catalogueElement'+recordPortionMappingId;
        console.log('fetching catalogue elements of ' + dataModelId);
        var url = '/recordCollectionMapping/catalogueElements/'+dataModelId+'?query='+query;
        getJSON(url, function(err, data) {
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
                yellowfade(document.getElementById(targetId).parentElement);
            }
        });
    }
    var greenHighlightedClass = "greenhighlighted";
    var highlightedClass = "highlighted";

    function greenfade(htmlElement) {
        fadeWithClass(htmlElement, greenHighlightedClass);
    }
    function fadeWithClass(htmlElement, cssClass) {
        removeHighlightedClasses(htmlElement);
        var elm = htmlElement;
        var newone = elm.cloneNode(true);
        newone.classList.add(cssClass);
        elm.parentNode.replaceChild(newone, elm);
    }
    function removeHighlightedClasses(htmlElement) {
        htmlElement.classList.remove(greenHighlightedClass);
        htmlElement.classList.remove(highlightedClass);
    }
    function yellowfade(htmlElement) {
        fadeWithClass(htmlElement, highlightedClass);
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
<nav class="navbar navbar-expand-lg navbar-light bg-light sticky-top">
    <g:render template="/templates/navbarBrand"/>
    <g:render template="/templates/logout"/>
</nav>

<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><g:link controller="recordCollection" action="index" ><g:message code="nav.home" default="Home"/></g:link></li>
        <li class="breadcrumb-item"><g:link controller="record" action="index" params="[recordCollectionId: recordCollectionId]">${recordCollectionEntity.datasetName}</g:link></li>
        <li class="breadcrumb-item active" aria-current="page"><g:message code="recordCollection.mapping" default="Record Collection Mapping"/></li>
    </ol>
</nav>

<g:render template="/templates/flashmessage"/>
<g:render template="/templates/flasherror"/>

<g:if test="${dataModelList}">
    <article>
    <h1>${recordCollectionEntity.datasetName}</h1>
    <h2><g:message code="recordCollection.dataModelName" default="DataModel" />: ${recordCollectionEntity.dataModelName}</h2>
    <g:if test="${recordPortionMappingList}">
            <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
            <g:hiddenField name="dataModelId" id="dataModelId" value="${recordCollectionEntity.dataModelId}"/>
            <table class="table table-striped">
                <thead class="thead-dark">
                <tr>
                    <th><g:message code="recordPortionMapping.header" default="Header"/></th>
                    <th><g:message code="recordPortionMapping.catalogueElement" default="Catalogue Element"/></th>
                </tr>
                </thead>
                <tbody>
                <g:each var="recordPortionMapping" in="${recordPortionMappingList}">
                    <td>${recordPortionMapping.header}</td>


                    <td>
                        <input type="text" name="catalogueElement${recordPortionMapping.id}Query" id="catalogueElement${recordPortionMapping.id}Query" />
                        <button type="button" onclick="onQueryChanged('${recordPortionMapping.id}')" class="btn btn-default"><g:message code="catalogueElements.filter" default="Filter"/></button>
                        <g:select
                            name="catalogueElement${recordPortionMapping.id}"
                            noSelection="${['null':'Select One...']}"
                            optionKey="gormUrl"
                            optionValue="name"
                            from="${catalogueElementList}"
                            value="${recordPortionMapping.gormUrl}"
                            onChange="onCatalogueElementChanged(${recordPortionMapping.id});"/>
                            <input type="hidden" disabled="disabled" id="gormUrl${recordPortionMapping.id}" name="gormUrl${recordPortionMapping.id}" value="${recordPortionMapping.gormUrl}" />
                            <span>
                                <button  id="saveButton${recordPortionMapping.id}" type="button" onclick="onSaveMapping('${recordPortionMapping.id}')" class="btn btn-save"><g:message code="headerMapping.save" default="Save"/></button>
                            </span>
                    </td>
                    </tr>
                </g:each>
                </tbody>
            </table>
    </g:if>
    <g:else>
        <div class="loader"></div>
    </g:else>
    <article>
</g:if>
</body>
</html>