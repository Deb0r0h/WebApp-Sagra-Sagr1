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

		<c:if test="${message.error}">
		    <c:import url="/jsp/include/show-message.jsp"/>
		</c:if>

        <form method="get" action="${isChecked ? '/payedorders' : '/orders'}">
            <label>Payed</label>
            <input type="checkbox" name="isChecked" ${isChecked ? 'checked' : ''}><br>

            <label for="id">ID SAGRA:</label>
            <input id="id" name="id_sagra" type="text"/><br/><br/>

            <label for="id">ID ORDER:</label>
            <input id="id" name="id_order" type="text"/><br/><br/>
            <button type="submit">Search</button><br/>
        </form>

        <br>

        <c:if test="${not empty orders_list}">
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
                    </tr>
                </thead>

                <tbody>
                    <c:forEach var="order" items="${orders_list}">
                        <tr>
                            <td><c:out value="${order.id}"/></a></td>
                            <td><c:out value="${order.client_name}"/></td>
                            <td><c:out value="${order.email}"/></td>
                            <td><c:out value="${order.client_num}"/></td>
                            <td><c:out value="${order.table_number}"/></td>
                            <td><c:out value="${order.id_user}"/></td>
                            <td><c:out value="${order.order_time}"/></td>
                            <td><c:out value="${order.payment_time}"/></td>
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
                                        <c:forEach var="content" items="${order.order_content}">
                                            <tr>
                                                <td><c:out value="${content.product_name}"/></td>
                                                <td><c:out value="${content.quantity}"/></td>
                                                <td><c:out value="${content.price}"/></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
	</body>
</html>