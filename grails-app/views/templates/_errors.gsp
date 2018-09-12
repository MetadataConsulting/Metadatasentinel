<g:if test="${errorBean}">
<g:hasErrors bean="${errorBean}">
    <g:eachError>
        <div class='alert alert-danger'>
            <g:message error="${it}"/>
        </div>
    </g:eachError>
</g:hasErrors>
</g:if>