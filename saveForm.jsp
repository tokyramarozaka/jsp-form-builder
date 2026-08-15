<%@ page import="model.FormBuilder" %>
<%@ page import="model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="repository" class="model.UserRepository" scope="application"/>
<%
    String className = request.getParameter("className");
    String error = null;
    User toCrupdate = null;

    if (className == null || className.trim().isEmpty()) {
        error = "No className was submitted with the form.";
    } else {
        try {
            toCrupdate = (User) FormBuilder.fromParameters(request.getParameterMap());
            repository.crupdate(toCrupdate);
        } catch (RuntimeException e) {
            error = "Invalid form data: " + e.getMessage();
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Saved/Updated</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <jsp:include page="navbar.jsp" />
    <main>
        <% if (error != null) { %>
            <h1>Something went wrong</h1>
            <p><strong><%= error %></strong></p>
        <% } else { %>
            <h1>Saved/Updated</h1>
            <p><%= toCrupdate %> has been sucessfully saved</p>
            <p><%= repository.findAll().size() %> user(s) stored so far.</p>
        <% } %>
    </main>
</body>
</html>
