<#import "/spring.ftl" as spring />

<html>
<head>
    <meta content="text/html;charset=utf-8" http-equiv="Content-Type">
    <meta content="utf-8" http-equiv="encoding">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
    <script src="<@spring.url '/js/bootstrap.js' />"></script>
    <script src="<@spring.url '/bootstrap/properties.json' />"></script>
</head>
<body>
<div>
    <form method=POST action="<@spring.url '/j_spring_security_check' />">
        <fieldset>
            <legend>Login</legend>
            <label for="username">Username</label>

            <div>
                <input name="j_username" type="text" id="log userName"/>
            </div>
            <label for="password">Password</label>

            <div>
                <div>
                    <input name="j_password" type="password" id="password"/>
                </div>
            </div>
            <br/>

            <div>
                <input type="submit" Submit/>
            </div>
        </fieldset>
    </form>
</div>
</body>
</html>