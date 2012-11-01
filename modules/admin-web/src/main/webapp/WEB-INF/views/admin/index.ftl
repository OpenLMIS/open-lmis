<#import "/spring.ftl" as spring />
<#assign sec=JspTaglibs["http://www.springframework.org/security/tags"] />


<html>
<head>
    <title>OpenLMIS - Admin</title>
</head>
<body onunload="">
    <h2>Hello Admin! Welcome <@sec.authentication property="principal" /></h2>
    <h4>
        <a href="<@spring.url '/j_spring_security_logout'/>" target="_parent">logout</a>
    </h4>
</body>
<head>
</head>
</html>
