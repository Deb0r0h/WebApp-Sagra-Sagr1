<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
	<head>
		<title>Delete Products</title>
	</head>

	<body>
		<h1>Delete Products</h1>
		<hr/>

        <form method="POST" enctype="multipart/form-data" action="<c:url value="/seeprod/delete"/>">
            <label for="Oldname">Name:</label>
              <input type="text" id="name" name="name" required><br>
              <label for="OldId_sagra">ID Sagra:</label>
              <input type="number" id="id_sagra" name="id_sagra" required><br>
            <button type="submit">Delete</button><br/>
        </form>
	</body>
</html>
