<#import "/spring.ftl" as spring />
<#assign sec=JspTaglibs["http://www.springframework.org/security/tags"] />

<html>
<head>
    <title>OpenLMIS</title>
    <#--<meta http-equiv="cache-control" content="max-age=0"/>-->
    <#--<meta http-equiv="cache-control" content="no-store"/>-->
    <#--<meta http-equiv="cache-control" content="no-cache"/>-->
    <meta http-equiv="expires" content="-1"/>
    <#--<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>-->
    <meta http-equiv="pragma" content="no-cache"/>
</head>
<body>
<h2>Hello User! Welcome <@sec.authentication property="principal" /></h2>
<h4><a href="<@spring.url '/j_spring_security_logout'/>">logout</a></h4>
</body>
</html>
