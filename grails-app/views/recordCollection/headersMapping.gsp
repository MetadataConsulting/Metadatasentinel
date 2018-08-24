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
    function getSelectValue(selectId) {
        var element = document.getElementById(selectId)
        if ( element != null ) {
            return element.value;
        }
        console.log('Not found value for' + selectId);
        $.notify({message: "Error finding select element "+selectId+"!"}, {type: 'danger'})

        return null
    }
    function replaceWith(id, html) {
        document.getElementById(id).innerHTML = html;
    }

    function saveMapping(recordPortionMappingId, headerName) {
        var targetId = 'catalogueElementSelectionForMapping'+recordPortionMappingId;
        var gormUrl = getSelectValue(targetId);
        var url = '/recordCollectionMapping/'+recordPortionMappingId+'/save?gormUrl='+gormUrl;
        // Call this URL. This triggers a save of the mapping for this header and returns a RecordCollectionMappingGormEntity.
        getJSON(url, function(err, data) {
            if (err != null) {
                console.log('Something went wrong: Error code '+err);
                $.notify({message: "Error saving mapping for header "+headerName+"!"}, {type: 'danger'})
            } else {
                $.notify({message: "Saved mapping for header "+headerName+"!"}, {type: 'success'})

            }
        });
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
    <h1 class="center">Mapping for Record Collection: ${recordCollectionEntity.datasetName}</h1>
    <h2 class="center"><g:message code="recordCollection.dataModelName" default="DataModel" />: ${recordCollectionEntity.dataModelName}</h2>


    <br/>
    <g:form controller="recordCollection" action="validate" method="POST" class="form-inline my-2 my-lg-0">
        <g:hiddenField name="recordId" value="${recordId}"/>
        <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
        <g:hiddenField name="datasetName" value="${datasetName}"/>
        <input type="submit" class="center big-btn btn btn-outline-success my-2 my-sm-0" value="${g.message(code: 'record.validate', default: 'Validate!')}"/>
    </g:form>
    <br/>


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
                    <div>
                            <g:select
                                    name="catalogueElementSelectionForMapping${recordPortionMapping.id}"
                                    noSelection="${['null':'Select One...']}"
                                    optionKey="gormUrl"
                                    optionValue="name"
                                    from="${catalogueElementList}"
                                    value="${recordPortionMapping.gormUrl}"
                                    onChange="saveMapping(${recordPortionMapping.id}, '${recordPortionMapping.header}');"/>

                        <input type="hidden" disabled="disabled" id="gormUrl${recordPortionMapping.id}" name="gormUrl${recordPortionMapping.id}" value="${recordPortionMapping.gormUrl}" />

                    </div>

                    <g:javascript>
                      console.log("#catalogueElementSelectionForMapping${recordPortionMapping.id}")
                        jQuery("#catalogueElementSelectionForMapping${recordPortionMapping.id}").selectize()

                    </g:javascript>
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
    <g:javascript>
        $(document).ready(function() {
            <g:each var="recordPortionMapping" in="${recordPortionMappingList}">


            </g:each>
        })
    </g:javascript>


</g:if>
</body>
</html>
