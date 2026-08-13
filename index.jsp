<jsp:useBean id="repository" class="model.UserRepository" scope="application"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>A patient and doctors app</title>
    <link rel="stylesheet" href="style.css">
</head>
<body> 
    <jsp:include page="navbar.jsp" />
    <main> 
        <h1>Patient & Doctor Management</h1> 
        <p>Manage doctors and patients from one place.</p> 
        <div class="actions"> 
            <a href="form.jsp?className=model.Doctor">Doctors</a> 
            <a href="form.jsp?className=model.Patient">Patients</a> 
        </div> 
    </main> 
</body>
</html>