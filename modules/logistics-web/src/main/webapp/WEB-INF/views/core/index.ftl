<#import "/spring.ftl" as spring />
<#assign sec=JspTaglibs["http://www.springframework.org/security/tags"] />

<html>
<head>
    <title>OpenLMIS</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
    <script type="text/javascript">
        $.ajaxSetUp({cache:false});
    </script>
</head>
<#-- TODO: onunload clears cache for safari. It is a hack. Find better alternative -->
<body onunload="">
<h2>Hello User! Welcome <@sec.authentication property="principal" /></h2>
<h4><a href="<@spring.url '/j_spring_security_logout'/>">logout</a></h4>
</body>
</html>
