<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=utf-8" %>
<!DOCTYPE html>
<html>

  <c:choose>
    <c:when test="${empty sessionScope.admin}">
      <head>
        <title> Unauthorized access </title>
        <!-- FAVICON -->
        <c:import url="/jsp/headers/common-header-tags.jsp"/>
      </head>

      <body>
      <h1> UNAUTHORIZED ACCESS</h1>

      <p>You should not be here UNKNOWN USER</p>

      </body>
    </c:when>

    <c:otherwise>
      <head>


        <link href="<c:url value="/css/insertProduct.css"/>" type="text/css" rel="stylesheet"/>

        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <title>Insert Product Form</title>
        <!-- FAVICON -->
        <c:import url="/jsp/headers/common-header-tags.jsp"/>
      </head>
      <body>
      <form method="POST" id="insertForm" enctype="multipart/form-data" action="<c:url value="/insert"/>">
        <label for="name" class="block">Name</label>
        <input type="text" id="name" name="name" required pattern="^[^'&quot;]+$" oninput="validateName(this)" class="form-control form-control-lg" maxlength="50">
        <label for="description" class="block">Description</label>
        <textarea type="text" id="description" name="description" rows="4" class=" form-control form-control-lg" maxlength="100"></textarea>
        <label for="priceInsert" class="block">Price</label>
        <input type="text" id="priceInsert" name="price"  pattern="^[0-9]+([,.][0-9]+)?$" required   class="form-control form-control-lg block">

        <div class="form-check form-switch">
          <input class="form-check-input" type="checkbox" role="switch" name="bar" value="true" id="bar">
          <label class="form-check-label" for="bar">Bar</label>
        </div>

        <div class="form-check form-switch">
          <input class="form-check-input" type="checkbox" role="switch" name="available" value="true" id="available">
          <label class="form-check-label" for="available">Available</label>
        </div>


        <label for="categoryInsert" class="block">Category</label>
        <select name="category" id="categoryInsert" required  class="form-control form-control-lg">

        </select><br>

          <label for="photo-insert">Photo</label><br>
          <div style="display: flex; align-items: center;" class="form-control form-control-lg">
              <div id="photo-insert-dropzone" class="form-control form-control-lg">
                  <p>Drag and drop an image file here, or click to select</p>
                  <img id="preview-insert-image" src="#" alt="Preview">
                  <input type="file" accept="image/png, image/jpeg, .jpg, .jpeg, .png" id="photo-insert" name="photo" class="form-control form-control-lg">
                  <button id="remove-insert-image" class="btn btn-primary">Remove Image</button>
              </div>
          </div>

        <input type="submit"  id="submitInsert" value="Submit" class="btn btn-primary">


      </form>

      <script src="<c:url value="/js/insertProduct.js"/>"></script>

      </body>
    </c:otherwise>
  </c:choose>
</html>
