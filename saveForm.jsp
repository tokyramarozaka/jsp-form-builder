<%@ page import="form.FormBuilder" %>
<%@ page import="model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="repository" class="model.UserRepository" scope="application"/>
<%
    String className = request.getParameter("className");
    String error = null;
    User saved = null;

    if (className == null || className.trim().isEmpty()) {
        error = "No className was submitted with the form.";
    } else {
        try {
            Class<?> modelClass = Class.forName(className.trim());
            saved = (User) FormBuilder.fromParameters(modelClass, request.getParameterMap());
            repository.add(saved);
        } catch (ClassNotFoundException e) {
            error = "Class not found: " + className;
        } catch (ReflectiveOperationException e) {
            Throwable cause = (e.getCause() != null) ? e.getCause() : e;
            error = "Could not build object: " + cause.getMessage();
        } catch (RuntimeException e) {
            error = "Invalid form data: " + e.getMessage();
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Saved</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
<main>

<% if (error != null) { %>
    <h1>Something went wrong</h1>
    <p><strong><%= error %></strong></p>
<% } else { %>
    <h1>Saved</h1>
    <p><%= saved %></p>
    <p><%= repository.findAll().size() %> user(s) stored so far.</p>
<% } %>

<div class="actions">
    <a href="form.jsp?className=model.Patient">New patient</a>
    <a href="form.jsp?className=model.Doctor">New doctor</a>
</div>

</main>
</body>
</html>
