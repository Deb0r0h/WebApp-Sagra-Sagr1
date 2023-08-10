<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
  <title>Insert Product Form</title>
</head>
<body>

<h1>Update Product Form</h1>
<form  method="POST" enctype="multipart/form-data" action="<c:url value="/seeprod/update"/>">
  <label for="Oldname">Old Name:</label>
  <input type="text" id="Oldname" name="Oldname" required><br>
  <label for="OldId_sagra">Old ID Sagra:</label>
  <input type="number" id="OldId_sagra" name="OldId_sagra" required><br>
  <label for="name">Name:</label>
  <input type="text" id="name" name="name"><br>
  <label for="id_sagra">ID Sagra:</label>
  <input type="number" id="id_sagra" name="id_sagra" ><br>
  <label for="description">Description:</label>
  <input type="text" id="description" name="description"><br>
  <label for="price">Price:</label>
  <input type="number" id="price" name="price"><br>
  <label for="bar">Bar:</label>
  <input type="checkbox" id="bar" name="bar" value="true"><br>
  <label for="available">Available:</label>
  <input type="checkbox" id="available" name="available" value="true" checked><br>
  <label for="category">Category:</label>
  <input type="text" id="category" name="category" ><br>
  <label for="photo">Photo:</label>
  <input type="file" accept="image/png, image/jpeg, .jpg, .jpeg, .png" id="photo" name="photo"><br>

  <input type="submit" value="Submit">
  <!--<button type="submit">Submit</button><br/> -->
</form>
</body>
</html>
