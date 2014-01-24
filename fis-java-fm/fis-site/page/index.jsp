<%@page session="false"%>
<%@ page contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="cn.tianya.fw.front.*,cn.tianya.fw.front.util.*, java.util.*" %>
<%@ taglib uri="/fis" prefix="fis"%> 
<%
//建立数据模型
Map<String, Object> root = new HashMap<String, Object>();
// 放入对应数据key value
Map<String, Object> data = FisModelSimulator.getJSON("./data/fis_data.json");
Map<String, Object> bbslist = FisModelSimulator.getJSON("./data/bbs_data.json");
Map<String, Object> data2 = FisModelSimulator.getJSON("./data/test_data.json");

root.putAll(data);
root.putAll(data2);
root.putAll(bbslist);
List<Map<String,Object>> docs = (List<Map<String,Object>>)root.get("docs");
List<Map<String,Object>> actList = (List<Map<String,Object>>)root.get("bbslist");
%>
<%-- 使用<fis:html>标签替代传统<html>标签，并设置map.json文件部署路径，缺省是“/” --%>
<fis:html>   
<head> 
    <meta charset="utf-8"/>
    <meta content="<%= root.get("description") %>" name="description">
    <title><%= root.get("title") %></title>
    <!--[if lt IE 9]>
        <script src="/lib/js/html5.js"></script>
    <![endif]-->
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
    <fis:require id="lib/css/bootstrap.css" />
    <fis:require id="lib/css/bootstrap-responsive.css" />
    <fis:require id="lib/js/mod.js" />
    <fis:require id="lib/js/jquery-1.10.1.js" />
    <fis:styles/>
</head>
<body>
    <div id="wrapper">
        <div id="sidebar">
        	<%@ include file="../widget/sidebar/sidebar.jsp"%> 
        </div> 
        <div id="container">
        	<%@ include file="../widget/slogan/slogan.jsp"%> 
        	<%@ include file="../widget/section/section.jsp"%> 
        </div>
    </div>
    <fis:require id="page/index.css" />
    <fis:script>var _bdhmProtocol = (("https:" == document.location.protocol) ? " https://" : " http://");
document.write(unescape("%3Cscript src='" + _bdhmProtocol + "hm.baidu.com/h.js%3F70b541fe48dd916f7163051b0ce5a0e3' type='text/javascript'%3E%3C/script%3E"));</fis:script>
<fis:script>var _bdhmProtocol3 = (("https:" == document.location.protocol) ? " https://" : " http://");
document.write(unescape("%3Cscript src='" + _bdhmProtocol + "hm.baidu.com/h.js%3F70b541fe48dd916f7163051b0ce5a0e3' type='text/javascript'%3E%3C/script%3E"));</fis:script>
	
	<fis:scripts/>
</body>
</fis:html>