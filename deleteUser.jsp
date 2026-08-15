<%@ page import="java.util.UUID" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="repository" class="model.UserRepository" scope="application"/>
<%
    repository.deleteById(UUID.fromString(request.getParameter("id")));
    response.sendRedirect("list.jsp");
%>