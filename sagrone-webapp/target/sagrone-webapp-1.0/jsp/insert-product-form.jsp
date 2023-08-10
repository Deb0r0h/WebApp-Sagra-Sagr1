<!--
Copyright 2018-2023 University of Padua, Italy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Author: Gabriel Taormina (gabriel.taormina@studenti.unipd.it)
Version: 1.0
Since: 1.0
-->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>Insert Product Form</title>
</head>
<body>

<h1>Insert Product Form</h1>
<form method="POST" enctype="multipart/form-data" action="<c:url value="/insert"/>">
  <label for="name">Name:</label>
  <input type="text" id="name" name="name" required><br>

  <%--<label for="id_sagra">ID Sagra:</label>
  <input type="number" id="id_sagra" name="id_sagra" required><br>--%>

  <label for="description">Description:</label>
  <input type="text" id="description" name="description"><br>
  <label for="price">Price:</label>
  <input type="number" id="price" name="price"><br>
  <label for="bar">Bar:</label>
  <input type="checkbox" id="bar" name="bar" value="true"><br>
  <label for="available">Available:</label>
  <input type="checkbox" id="available" name="available" value="true"><br>


  <label for="category">Category:</label>
  <select name="category" id="category" required>
    <option value="first course">First course</option>
    <option value="starters">Starters</option>
    <option value="meat">Meat</option>
    <option value="beverages">Beverages</option>
    <option value="side dishes">Side dishes</option>
  </select><br>
  <!--<input type="text" id="category" name="category" required><br>-->

  <label for="photo">Photo:</label>
  <input type="file" accept="image/png, image/jpeg, .jpg, .jpeg, .png" id="photo" name="photo"><br>
  <%--<label for="quantity">Quantity:</label>
  <input type="number" id="quantity" name="quantity"><br>--%>

  <input type="submit" value="Submit">
  <!--<button type="submit">Submit</button><br/> -->

</form>
</body>
</html>
