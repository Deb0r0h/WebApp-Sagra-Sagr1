<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
	<head>
		<title>Orders</title>
	</head>

	<body>
		<h1>Orders</h1>
		<hr/>

        <!-- display the message -->
        <c:if test="${not empty messages}">
            <c:forEach var="message" items="${messages}">
                <c:if test="${message.error}">
                    <c:import url="/jsp/include/show-message.jsp"/>
                </c:if>
        	</c:forEach>
        </c:if>

       <form method="get" id="myForm">
           <label>Payed</label>
           <input type="checkbox" name="isChecked" id="isChecked" ${isChecked ? 'checked' : ''} onchange="updateFormAction()"><br>

           <label for="id">ID ORDER:</label>
           <input id="id" name="IDorder" type="text"/><br/><br/>
           <button type="submit">Search</button><br/>
       </form>

        <br>
        <a href="${pageContext.request.contextPath}">BASE URL</a>

        <c:if test="${not empty orders}">
            <table>
                <thead>
                    <tr>
                        <th>ID:</th>
                        <th>Client Name:</th>
                        <th>Email:</th>
                        <th>Number of Clients:</th>
                        <th>Number of Table:</th>
                        <th>ID User:</th>
                        <th>Order time:</th>
                        <th>Payment Time:</th>
                        <th>Order Content</th>
                        <th>Operation:</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach var="order" items="${orders}">
                        <tr>
                            <td><c:out value="${order.id}"/></td>
                            <td><c:out value="${order.clientName}"/></td>
                            <td><c:out value="${order.email}"/></td>
                            <td><c:out value="${order.clientNum}"/></td>
                            <td><c:out value="${order.tableNumber}"/></td>
                            <td><c:out value="${order.idUser}"/></td>
                            <td><c:out value="${order.orderTime}"/></td>
                            <td><c:out value="${order.paymentTime}"/></td>
                            <td>
                                <table>
                                    <thead>
                                        <tr>
                                            <th>Order Name</th>
                                            <th>Quantity</th>
                                            <th>Price</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="content" items="${order.orderContent}">
                                            <tr>
                                                <td><c:out value="${content.productName}"/></td>
                                                <td><c:out value="${content.quantity}"/></td>
                                                <td><c:out value="${content.price}"/> €</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </td>
                            <td>
                            <form method="GET" action="<c:url value='menu/mod'/>">
                                <input type="hidden" name="IDorder" value="${order.id}" />
                                <button type="submit">MOD</button><br/>
                            </form>
                            <form method="POST" action="<c:url value='${(order.idUser > 0) ? "/payedorders/unpay" : "/orders/pay"}'/>">
                                <input type="hidden" name="IDorder" value="${order.id}" />
                                <button type="submit">${(order.idUser > 0) ? 'UNPAY' : 'PAY'}</button><br/>
                            </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
	</body>
	<script>
    function updateFormAction() {
        const form = document.getElementById('myForm');
        const isChecked = document.getElementById('isChecked').checked;
        const actionUrl = isChecked ? 'payedorders' : 'orders';
        form.setAttribute('action', actionUrl);
    }
    </script>
</html>