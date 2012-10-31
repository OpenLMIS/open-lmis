    <#import "/spring.ftl" as spring />

    <html xmlns="http://www.w3.org/1999/html">
    <head>
        <meta content="text/html;charset=utf-8" http-equiv="Content-Type">
        <meta content="utf-8" http-equiv="encoding">
        <title>OpenLMIS</title>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
        <script src="<@spring.url '/js/bootstrap.js' />"></script>
        <script src="<@spring.url '/bootstrap/properties.json' />"></script>
    </head>
    <body style="margin-right: 4; background-color: #f5f5f5;">
    <div id="errorSection">
        <#if  RequestParameters.error??>
            <#if "${RequestParameters.error}"=="true">
                The username or password you entered is incorrect . Please try again.
            </#if>
        </#if>
    </div>
        <div>
            <form style="margin-top: 10; background-color: #ffffff;" method=POST action="<@spring.url '/j_spring_security_check' />">
                <label style="margin-bottom: 4; font-weight: bold;">Sign In</label>
                <br/>
                <br/>
                <fieldset>
                    <label style="padding-top: 10" for="username">Username</label>

                    <div>
                        <input name="j_username" type="text" id="username" style="width: 15%"/>
                    </div>
                    <br/>

                    <label for="password" style="margin-right: 5%">Password</label>
                    <a href="#">Forgot password?</a>

                    <div>
                        <input name="j_password" type="password" id="password" style="width: 15%"/>
                    </div>
                    <br/>

                    <div>
                        <input type="submit" value="Sign in"/>
                    </div>
                </fieldset>
            </form>
        </div>
    </body>
    </html>