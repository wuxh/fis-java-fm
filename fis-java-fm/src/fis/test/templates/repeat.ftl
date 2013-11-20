<#assign x = 1>   
<@html class="html5">  
<@head calss="head">
	<meta charset="utf-8"/>
    <meta content="{%$description%}" name="description">
    <title>{%$title%}</title>
    <!--[if lt IE 9]>
        <script src="/lib/js/html5.js"></script>
    <![endif]-->
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
    <@require name="lib/css/bootstrap.css" />
    <@require name="lib/css/bootstrap-responsive.css" />
    <@require name="lib/js/mod.js" />
    <@require name="lib/js/jquery-1.10.1.js" />
</@head>
<@body class="body bg">
一个参数：   
<@repeat count=4>   
  Test ${x}   
  <#assign x = x + 1>   
</@repeat>   
  
二个参数：   
<@repeat count=3 hr=true>   
  Test   
</@repeat>   
  
循环变量：   
<@repeat count=3; cnt>   
  ${cnt}. Test   
</@repeat>   

test:
<@widget name="widget/nav/nav.ftl" />   


<@script>
var _bdhmProtocol1 = (("https:" == document.location.protocol) ? " https://" : " http://");
document.write(unescape("%3Cscript src='" + _bdhmProtocol + "hm.baidu.com/h.js%3F70b541fe48dd916f7163051b0ce5a0e3' type='text/javascript'%3E%3C/script%3E"));
</@script>
<@script>
var _bdhmProtocol2 = (("https:" == document.location.protocol) ? " https://" : " http://");
document.write(unescape("%3Cscript src='" + _bdhmProtocol + "hm.baidu.com/h.js%3F70b541fe48dd916f7163051b0ce5a0e3' type='text/javascript'%3E%3C/script%3E"));
</@script>

</@body>
</@html>