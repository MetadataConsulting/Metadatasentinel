<sec:ifLoggedIn>
        <div class="btn"><g:message code="user.logged" default="Logged in as " /><b><sec:username /></b></div>
        <div class="nav-item">
            <g:form controller='logout' action="index"><g:submitButton class="btn btn-primary" name="logout" value="Logout"/></g:form>
        </div>
</sec:ifLoggedIn>