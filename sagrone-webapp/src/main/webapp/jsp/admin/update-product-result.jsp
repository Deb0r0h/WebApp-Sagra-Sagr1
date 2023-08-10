<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
	<title>Update Product</title>
	<!-- FAVICON -->
	<c:import url="/jsp/headers/common-header-tags.jsp"/>
</head>

<body>
<h1>Update Product</h1>
<hr/>

<!-- display the message -->
<c:import url="/jsp/include/show-message.jsp"/>

<!-- display the just updated product, if any and no errors -->
<c:if test="${not empty product && !message.error}">
	<ul>
		<li>name: <c:out value="${product.name}"/></li>
		<li>id_sagra: <c:out value="${product.idSagra}"/></li>
		<li>description: <c:out value="${product.description}"/></li>
		<li>price: <c:out value="${product.price}"/></li>
		<li>bar: <c:out value="${product.bar}"/></li>
		<li>available: <c:out value="${product.available}"/></li>
		<li>category: <c:out value="${product.category}"/></li>

		<c:choose>
			<c:when test="${product.hasPhoto()}">

				<li>photo:
					<ul>
						<li>MIME media type: <c:out value="${product.photoType}"/> </li>
						<li>image: <br/>
							<img
									width="300" height="300" src="<c:url value="/load-product-photo/"><c:param name="sagra" value="${product.idSagra}"/><c:param name="name" value="${product.name}"/></c:url>"/>
						</li>
					</ul>
				</li>

			</c:when>

			<c:otherwise>
				<li>photo: not available</li>
			</c:otherwise>
		</c:choose>

	</ul>
</c:if>
</body>
</html>
