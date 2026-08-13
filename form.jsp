<%@ page import="model.FormBuilder" %>
<%@ page import="java.lang.reflect.Modifier" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String className = request.getParameter("className");
    String error = null;
    String formHtml = null;
    Class<?> modelClass = null;

    if (className == null || className.trim().isEmpty()) {
        error = "No className provided. Please provide either: ?className=model.Patient or ?className=model.Doctor";
    } else {
        try {
            modelClass = Class.forName(className.trim());
            if (Modifier.isAbstract(modelClass.getModifiers())) {
                error = "Cannot build a form for abstract class: '" + className + "'";
            } else {
                formHtml = FormBuilder.toHtml(modelClass, "saveForm.jsp");
            }
        } catch (ClassNotFoundException e) {
            error = "Class not found: " + className;
        } catch (RuntimeException e) {
            error = "Could not build form for '" + className + "': " + e.getMessage();
        }
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
