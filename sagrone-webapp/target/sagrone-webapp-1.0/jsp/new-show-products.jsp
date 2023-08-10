<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
	<head>
		<title>Products</title>

		<script>

            function openPopup(n, d, p, b, av, c) {
                var popup = window.open('jsp/new-update-product-form.jsp', 'popup', 'width=500,height=500');
                popup.onload = function() {

                    var oldname=popup.document.getElementById('Oldname');
                    var name = popup.document.getElementById('name');
                    var description = popup.document.getElementById('description');
                    var price = popup.document.getElementById('price');
                    var bar = popup.document.getElementById('bar');
                    var available = popup.document.getElementById('available');
                    var category = popup.document.getElementById('category');

                    oldname.value = n;
                    name.value = n;
                    description.value = d;
                    price.value = p;
                    bar.value = b;
                    available.value = av;
                    category.value = c;

                };

            }
        </script>


	</head>

	<body>
		<h1>Products</h1>
		<hr/>

        <form method="GET" action="<c:url value="/seeprod"/>">

            <button type="submit">Show</button><br/>
        </form>

        <br>

        <c:if test="${not empty productList}">
            <table>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Price</th>
                        <th>Bar</th>
                        <th>Available</th>
                        <th>Category</th>
                        <th>Photo</th>
                        <th>Photo Type</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach var="product" items="${productList}">
                        <tr>

                            <td><c:out value="${product.name}"/></td>
                            <td><c:out value="${product.description}"/></td>
                            <td><c:out value="${product.price}"/></td>
                            <td><c:out value="${product.bar}"/></td>
                            <td><c:out value="${product.available}"/></td>
                            <td><c:out value="${product.category}"/></td>

                            <td>
                                <c:choose>
                                <c:when test="${product.hasPhoto()}">

                                <img width="50" height="50" src="<c:url value="/load-product-photo"><c:param name="id_sagra" value="${product.idSagra}"/><c:param name="name" value="${product.name}"/></c:url>"/></td>
                                </c:when>
                                <c:otherwise>
                                          <c:out value="photo: not available"/>
                                </c:otherwise>
                                </c:choose>

                            <td><c:out value="${product.photoType}"/></td>

                          <td><button type="button" onclick="openPopup('${product.name}', '${product.description}', '${product.price}', '${product.bar}', '${product.available}', '${product.category}')">Modify</button></td>
                          </form>

                          <td>
                                <form method="POST" enctype="multipart/form-data" action="<c:url value="/seeprod/delete"/>">
                                    <input type="hidden" id="Oldname" name="Oldname" value="${product.name}"/>
                                    <input type="submit" id="sub" value="Delete">
                                </form>

                          </td>
                      </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>

	</body>
</html>
