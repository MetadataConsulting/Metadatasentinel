<g:if test="${flash.error}">
    <div class="alert alert-danger">
        <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
        <p>${flash.error.encodeAsRaw()}</p>
    </div>
</g:if>