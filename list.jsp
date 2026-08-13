<%@ page import="model.User" %>
<%@ page import="model.UserRepository" %>
<%@ page import="model.Doctor" %>
<%@ page import="model.Patient" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.stream.Collectors" %>

<jsp:useBean id="repository" class="model.UserRepository" scope="application"/>

<%
    List<Doctor> doctors = repository.findAll().stream()
            .filter(user -> user instanceof Doctor)
            .map(user -> (Doctor) user)
            .collect(Collectors.toList());

    List<Patient> patients = repository.findAll().stream()
            .filter(user -> user instanceof Patient)
            .map(user -> (Patient) user)
            .collect(Collectors.toList());
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Users</title>
    <link rel="stylesheet" href="style.css">
</head>

<body>
    <jsp:include page="navbar.jsp" />

    <main>
        <h1>Users</h1>

        <section>
            <h2>Doctors</h2>

            <div class="user-list">
                <% for (Doctor doctor : doctors) { %>

                    <div class="user-card">
                        <h3><%= doctor.getName() %></h3>

                        <p>
                            <strong>Email:</strong>
                            <%= doctor.getMail() %>
                        </p>

                        <p>
                            <strong>Gender:</strong>
                            <%= doctor.getGender() %>
                        </p>

                        <p>
                            <strong>Birthday:</strong>
                            <%= doctor.getBirthday() %>
                        </p>

                        <p>
                            <strong>Specialities:</strong>
                            <%= doctor.getSpecialities() %>
                        </p>

                        <p>
                            <strong>Experience:</strong>
                            <%= doctor.getExperience() %> years
                        </p>
                    </div>

                <% } %>
            </div>
        </section>

        <section>
            <h2>Patients</h2>

            <div class="user-list">
                <% for (Patient patient : patients) { %>

                    <div class="user-card">
                        <h3><%= patient.getName() %></h3>

                        <p>
                            <strong>Email:</strong>
                            <%= patient.getMail() %>
                        </p>

                        <p>
                            <strong>Gender:</strong>
                            <%= patient.getGender() %>
                        </p>

                        <p>
                            <strong>Birthday:</strong>
                            <%= patient.getBirthday() %>
                        </p>

                        <p>
                            <strong>Disease:</strong>
                            <%= patient.getDisease() %>
                        </p>

                        <p>
                            <strong>Severity:</strong>
                            <%= patient.getSeverity() %>
                        </p>
                    </div>

                <% } %>
            </div>
        </section>

    </main>

</body>

</html>