<!--
Copyright 2023 University of Padua, Italy

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

<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Insert Product Result</title>
</head>

<body>
<h1>Insert Product Result</h1>
<hr/>

<!-- display the message -->
<c:import url="/jsp/include/show-message.jsp"/>

<!-- display the just inserted product, if any and no errors -->
<c:if test="${not empty product && !message.error}">
    <ul>
        <li>name: <c:out value="${product.name}"/></li>
<%--        <li>id_sagra: <c:out value="${product.idSagra}"/></li>--%>
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
                                    src="<c:url value="/load-product-photo/"><c:param name="id_sagra" value="${product.idSagra}"/><c:param name="name" value="${product.name}"/></c:url>"/>
                        </li>
                    </ul>
                </li>

            </c:when>

            <c:otherwise>
                <li>photo: not available</li>
            </c:otherwise>
        </c:choose>

        <%--<li>quantity: <c:out value="${product.quantity}"/></li>--%>

    </ul>
</c:if>
</body>
</html>
