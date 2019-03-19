<html>

<head>
    <meta name='layout' content='main'/>
    <title><g:message code='sentinel.register.title' default="Register"/></title>
</head>

<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light justify-content-between">
    <g:render template="/templates/navbarBrand"/>
    <g:render template="/templates/logout"/>
</nav>

    <div class="col-md-6 col-md-offset-3">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><g:message code="springSecurity.oauth.registration.create.legend" default="Create a new account"/></h3>
            </div>

            <div class="panel-body">

                <g:render template="/templates/errors" model="[errorBean: command]"/>
                <g:render template="/templates/flashmessage"/>

                <g:form action="register" method="post" autocomplete="off" class="form-horizontal">
                    <div class="form-group ${hasErrors(bean: command, field: 'username', 'has-error')} ">
                        <label for='username-new' class="control-label col-sm-3"><g:message
                                code="user.username.label" default="Username"/>:</label>

                        <div class="col-sm-9">
                            <input type='text' class='form-control' name='username' id="username-new"
                                   value='${command?.username}'/>
                        </div>
                    </div>

                    <div class="form-group ${hasErrors(bean: command, field: 'email', 'has-error')} ">
                        <label for='email-new' class="control-label col-sm-3"><g:message code="user.email.label"
                                                                                         default="E-mail"/>:</label>

                        <div class="col-sm-9">
                            <input type='text' class='form-control' name='email' id="email-new" value='${command?.email ?: params.email}'/>
                        </div>
                    </div>

                    <div class="form-group ${hasErrors(bean: command, field: 'password', 'has-error')} ">
                        <label for='password' class="control-label col-sm-3"><g:message code="user.password.label" default="Password"/>:</label>

                        <div class="col-sm-9">
                            <input type='password' class='form-control' name='password' id="password"
                                   value='${command?.password}'/>
                        </div>
                    </div>

                    <div class="form-group ${hasErrors(bean: command, field: 'repeatPassword', 'has-error')} ">
                        <label for='repeatPassword' class="control-label col-sm-3"><g:message code="user.repeatPassword.label"
                                                                                         default="Password (Again)"/>:</label>

                        <div class="col-sm-9">
                            <input type='password' class='form-control' name='repeatPassword' id="repeatPassword"
                                   value='${command?.repeatPassword}'/>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-sm-offset-3 col-sm-9">
                            <button type="submit" class="btn btn-primary"><i
                                    class="glyphicon glyphicon-user"></i> <g:message
                                    code="sentinel.registration.create.button" default="Create"/></button>
                        </div>
                    </div>
                </g:form>
            </div>
        </div>
    </div>

<script>
    $(document).ready(function () {
        $('#username').focus();
    });
</script>

</body>
</html>
