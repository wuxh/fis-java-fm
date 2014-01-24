<%@page session="false"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
for(int i = 0; i < docs.size();i++){
	Map<String,Object> doc = docs.get(i);
%>
    <section class="section">
        <div class="container-fluid">
            <div class="row-fluid title" id="section-<%=i %>">
                <h2><%=doc.get("title") %></h2>
            </div>
            <div class="row-fluid content">
	            <%String docName = "/widget/section/docs/" + doc.get("doc")+ ".jsp"; %>  
            	<jsp:include page="<%=docName %>"></jsp:include>  

                <a href="<%=doc.get("wiki") %>" target="_blank" class="btn btn-primary pull-right">
                    了解更多
                    <i class="icon-circle-arrow-right icon-white"></i>
                </a>
            </div>
        </div>
    </section>
<%}%>	