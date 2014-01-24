<%@page session="false"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<nav id="nav" class="navigation" role="navigation">
    <ul>
        <% 
      		
        	for(int i = 0; i < docs.size();i++){
        		Map<String,Object> doc = docs.get(i);

        %>
        <li class="active">
            <a href="#section-<%= i %>">
                <i class="icon-<%= doc.get("icon") %> icon-white"></i> <span><%= doc.get("title") %></span>
            </a>
        </li>
        <%} %>
    </ul>
</nav>