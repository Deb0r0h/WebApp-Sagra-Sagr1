<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
	<head>
		<title>Products</title>
	</head>

	<body>
		<h1>Products</h1>
		<hr/>

        <form method="GET" action="<c:url value="/seeprod"/>">
            <label for="id">ID SAGRA:</label>
            <input id="id" name="id_sagra" type="text"/><br/><br/>
            <button type="submit">Search</button><br/>
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

                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
	</body>
</html>
