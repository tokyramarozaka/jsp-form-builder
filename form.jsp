<%@ page import="model.FormBuilder" %>
<%@ page import="java.lang.reflect.Modifier" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="repository" class="model.UserRepository" scope="application" />
<%
    String className = request.getParameter("className");
    String error = null;
    String formHtml = null;
    Class<?> modelClass = null;

    if (className == null || className.trim().isEmpty()) {
        error = "No className provided. Please provide either: ?className=model.Patient or ?className=model.Doctor";
    } else {
        String id = request.getParameter("id");
        modelClass = Class.forName(className.trim());
        formHtml = FormBuilder.toCreateOrUpdateHtmlFormByUserId(modelClass, id, repository); 
    }
    
%>
<!DOCTYPE html>
<html>
<head>
    <title>
        Form<% if (className != null) { %> - <%= className %><% } %>
    </title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <jsp:include page="navbar.jsp" />
    <% if (error != null) { %>
        <p><strong><%= error %></strong></p>
    <% } else { %>
        <h2><%= modelClass.getSimpleName() + " Form" %></h2>
        <%= formHtml %>
    <% } %>

</body>
</html>
