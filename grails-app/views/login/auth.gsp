<html>
<head>
    <meta name="layout" content="${gspLayout ?: 'main'}"/>
    <title><g:message code='springSecurity.login.title'/></title>
</head>

<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light sticky-top">
    <g:render template="/templates/navbarBrand"/>
    <g:render template="/templates/logout"/>
</nav>
<div class="container">
    <div class="row align-items-center">
        <div class="col">
        </div>
        <div class="col" id="login">
            <div class="fheader"><h1><g:message code='springSecurity.login.header'/></h1></div>

            <g:render template="/templates/flashmessage"/>

            <form action="${postUrl ?: '/login/authenticate'}" method="POST" id="loginForm" class="cssform" autocomplete="off">
                <div class="form-group">
                    <label for="username"><g:message code='springSecurity.login.username.label'/>:</label>
                    <input type="text" class="text_" name="${usernameParameter ?: 'username'}" id="username"/>
                </div>

                <div class="form-group">
                    <label for="password"><g:message code='springSecurity.login.password.label'/>:</label>
                    <input type="password" class="text_" name="${passwordParameter ?: 'password'}" id="password"/>
                </div>

                <div class="form-group">
                    <input type="submit" class="btn btn-primary" id="submit" value="${message(code: 'springSecurity.login.button')}"/>
                </div>
            </form>        </div>
        <div class="col">
        </div>
    </div>
</div>


<script>
    (function() {
        document.forms['loginForm'].elements['${usernameParameter ?: 'username'}'].focus();
    })();
</script>
</body>
</html>