<html>
<head>
    <title><g:message code="recordCollection.mapping" default="Record Collection Mapping"/></title>

    <meta name="layout" content="main" />


    <g:if test="${!recordPortionMappingList}">
        <meta http-equiv="refresh" content="2" />
    </g:if>


    %{--<asset:javascript src="bower_components/remarkable-bootstrap-notify/dist/bootstrap-notify.js"/>--}%
    %{--<asset:javascript src="bower_components/selectize/dist/js/standalone/selectize.js"/>--}%
    %{--<asset:stylesheet src="css_bower_components/selectize.css"/>--}%
    %{--<asset:javascript src="bower_components/jQuery-Collapse/src/jquery.collapse.js"/>--}%

    <asset:javascript src="bootstrap-notify.js"/>
    <asset:javascript src="selectize.js"/>
    <asset:stylesheet src="selectize.css"/>
    <asset:javascript src="jquery.collapse.js"/>



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
        var combinedGormUrlName = getSelectValue(targetId);
        console.log(combinedGormUrlName)
        if (combinedGormUrlName != 'null' && combinedGormUrlName != '') {
            var gormUrlNameArray = combinedGormUrlName.split("####") // This is a bit of a hack... the combinedGormUrlName is a string split by ####... might be better to request the name by gormUrl from the MDX.
            var gormUrl = gormUrlNameArray[0]
            var name = gormUrlNameArray[1]
            var url = '/recordCollectionMapping/'+recordPortionMappingId+'/save?gormUrl='+gormUrl+'&name='+name;
            // Call this URL. This triggers a save of the mapping for this header and returns a RecordCollectionMappingEntryGormEntity
            getJSON(url, function(err, data) {
                if (err != null) {
                    console.log('Something went wrong: Error code '+err);
                    $.notify({message: "Error saving mapping for column "+headerName+"!"}, {type: 'danger'})
                } else {
                    $.notify({message: "Saved mapping for column "+headerName+"!"}, {type: 'success'})

                }
            });
        }
    }
</g:javascript>
<nav class="navbar navbar-expand-lg navbar-light bg-light justify-content-between">
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
<div class="jumbotron">

<div class="float-right" id="mapping-explanation">
    <h6 class="collapse-heading">What is a Mapping?</h6>
    <p>A Mapping specifies, for each Column of your Spreadsheet, an Element from the Model Catalogue, which contains Rules which will be used to Validate Entries under that Column.</p>
</div>

<h4>Suggested mappings from :</h4>
<p><i>${recordCollectionEntity.datasetName}</i> <g:if test="${recordCollectionEntity.dataModelName}">
    <g:message code="recordCollection.dataModelName" default="" />
    to attributes and rules contained in <i>${recordCollectionEntity.dataModelName}</i></g:if><g:else>to MDX Elements</g:else> </p>









    <g:javascript>
        $("#mapping-explanation").collapse({
            query: '.collapse-heading'
        })
    </g:javascript>

    <div class="row">
        <div class="col-sm-6">
            <g:form class="center" onsubmit="return confirm('This will overwrite the current mapping. Are you sure you want to regenerate the mapping?')"
                    controller="recordCollection" action="regenerateMapping" method="POST">
                <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
                <input type="submit" class="center btn btn-outline-warning btn-block" value="${g.message(code: 'record.regenerateMapping', default: 'Regenerate Mapping')}"/>
            </g:form>
        </div>
        <div class="col-sm-6">
            <g:form class="center" controller="recordCollection" action="validate" method="POST" >
                <g:hiddenField name="recordId" value="${recordId}"/>
                <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
                <g:hiddenField name="datasetName" value="${datasetName}"/>
                <input type="submit" class="center btn btn-outline-success btn-block" value="${g.message(code: 'record.validate', default: 'Validate!')}"/>
            </g:form>
        </div>

    </div>




    </div>
    <g:if test="${recordPortionMappingList}">
        <g:hiddenField name="recordCollectionId" value="${recordCollectionId}"/>
        <g:hiddenField name="dataModelId" id="dataModelId" value="${recordCollectionEntity.dataModelId}"/>
        <table class="table table-striped">
            <thead class="thead-dark">
            <tr>
                <th><g:message code="recordPortionMapping.header" default="Column"/></th>
                <th><g:message code="recordPortionMapping.catalogueElement" default="Validates Against"/></th>
            </tr>
            </thead>
            <tbody>
            <g:each var="recordPortionMapping" in="${recordPortionMappingList}">
                <td>${recordPortionMapping.header}</td>


                <td>
                    <div>
                            <g:select
                                    name="catalogueElementSelectionForMapping${recordPortionMapping.id}"
                                    noSelection="${['':'Select One...']}"
                                    optionKey="combinedGormUrlName"
                                    optionValue="name"
                                    from="${catalogueElementList}"
                                    value="${recordPortionMapping.combinedGormUrlName}"
                                    onChange="saveMapping(${recordPortionMapping.id}, '${recordPortionMapping.header}');"
                                    >
                            </g:select>

                        <input type="hidden" disabled="disabled" id="gormUrl${recordPortionMapping.id}" name="gormUrl${recordPortionMapping.id}" value="${recordPortionMapping.gormUrl}" />

                    </div>

                    <g:javascript>
                      console.log("#catalogueElementSelectionForMapping${recordPortionMapping.id}")
                        jQuery("#catalogueElementSelectionForMapping${recordPortionMapping.id}").selectize({
                            valueField: 'combinedGormUrlName',
                            labelField: 'name',
                            searchField: 'name',
                            options: [],
                            create: false,
                            render: {
                                option: function(item /*: ElasticSearchCatalogueElementDocumentProjection */, escape) {
                                    var description = ""
                                    if (item.description) {
                                        description = "<br/><ul><li><i>"+escape(item.description)+"</i></li></ul>"
                                    }
                                    return '<div>'+escape(item.name)+description+'</div>'
                                }
                            },
                            load: function(query, callback) {
                                if (!query.length) return callback();
                                var url = "/fetch/mdxSearch?dataModelId=${recordCollectionEntity.dataModelId}&query="+query+"&searchImports=false"


                                $.ajax({
                                    url: url,
                                    type: 'GET',
                                    dataType: 'json',
                                    error: function(jqXHR, textStatus, errorThrown) {
                                        $.notify({message: "Request failed with textStatus "+textStatus+"!"}, {type: 'danger'})
                                        callback();
                                    },
                                    success: function(mdxSearchResponse /*: MDXSearchResponse */) {
                                        callback(mdxSearchResponse.list);
                                    }
                                });
                            }
                        })

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


</g:if>
</body>
</html>
