<sec:ifLoggedIn>

                <g:form class="form-inline my-2 my-lg-0 navbar-right" controller='logout' action="index">
                    <div class="btn"><g:message code="user.logged" default="Logged in as " /><b><sec:username /></b></div>
                    <g:submitButton class="btn btn-primary" name="logout" value="Logout"/>
                </g:form>

</sec:ifLoggedIn>
