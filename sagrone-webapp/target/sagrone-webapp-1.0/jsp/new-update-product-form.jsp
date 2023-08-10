<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
  <title>Insert Product Form</title>
</head>
<body>

<h1>Update Product Form</h1>
<form  id="UpdateForm" method="POST" enctype="multipart/form-data" action="<c:url value="/seeprod/update"/>">

  <input type="hidden" id="Oldname" name="Oldname">
  <label for="name">Name:</label>
  <input type="text" id="name" name="name"><br>
  <label for="description">Description:</label>
  <input type="text" id="description" name="description"><br>
  <label for="price">Price:</label>
  <input type="number" id="price" name="price"><br>
  <label for="bar">Bar:</label>
  <input type="checkbox" id="bar" name="bar" ><br>
  <label for="available">Available:</label>
  <input type="checkbox" id="available" name="available" checked><br>
  <label for="category">Category:</label>
  <select name="category" id="category">
      <option value="first course">First course</option>
      <option value="starters">Starters</option>
      <option value="meat">Meat</option>
      <option value="beverages">Beverages</option>
      <option value="side dishes">Side dishes</option>
    </select><br>
  <label for="photo">Photo:</label>
  <input type="file" accept="image/png, image/jpeg, .jpg, .jpeg, .png" id="photo" name="photo"><br>

  <input type="submit" id="UpdateSub" value="Submit">
  <!--<button type="submit">Submit</button><br/> -->
</form>

</body>
</html>
