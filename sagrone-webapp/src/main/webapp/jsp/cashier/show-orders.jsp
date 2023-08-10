<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">

    <c:choose>
        <c:when test="${empty sessionScope.cashier}">
            <head>
               <title> Unauthorized acces - You should not be here</title>
            </head>

            <body>
            <h1> UNAUTHORIZED ACCESS</h1>
            <p>You should not be here UNKNOWN USER</p>
            </body>
        </c:when>

        <c:otherwise>

            <head>
                <title>Orders</title>

                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <!-- Adding bootstrap -->

                <%@ include file="/jsp/include/bootstrap.jsp" %>
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
                <!-- PDF  -->
                <script src="https://cdnjs.cloudflare.com/ajax/libs/FileSaver.js/2.0.5/FileSaver.min.js"></script>


                <!-- CSS -->
                <link href="<c:url value="/css/base.css"/>"  type="text/css" rel="stylesheet"/>
                <link href="<c:url value="/css/show-orders.css"/>"  type="text/css" rel="stylesheet"/>

                <!-- JS -->
                <script src="<c:url value="/js/Popup.js"/>"></script>
                <script src="<c:url value="/js/showOrder.js"/>"></script>

                <!-- FAVICON -->
                <c:import url="/jsp/headers/common-header-tags.jsp"/>
            </head>

            <body class="bg-light">
                <%@ include file="/jsp/headers/header.jsp" %>


                <div class="container-fluid bg-light m-0 containerOrders text-center">

                    <!-- display the message -->
                    <c:if test="${message.error}">
                        <div class="row justify-content-md-center m-3 p-3 mb-1 pt-1">
                        <c:import url="/jsp/include/show-message.jsp"/>
                        </div>
                    </c:if>

                    <!-- Check if confirmation attribute is present -->
                    <c:if test="${not empty confirmation}">
                        <div class="card bg-light m-2 p-1 mx-5 px-4 w-auto" id="confirmationContainer">
                            <div id="progressBar"></div>
                            <h6 class="mx-4 px-3 my-2 py-1">ORDER N:${confirmation.message} successfully paid </h6>
                        </div>


                        <script>
                            openPopupOnConfirmation("SUCCESS",
                                                    "Order paid successfully",
                                                    () => sendPDFRequest("<c:url value='/DownloadCashierPDF'/> ","${confirmation.message}" , "${sessionScope.sagra}"),
                                                    "success",
                                                    "Download PDF");
                        </script>


                    </c:if>

                    <!-- FORM -->
                    <div class="row justify-content-md-center m-3 p-3 mb-1 pt-1">
                        <div class="col-md-8 m-0">
                            <form method="get" id="myForm" class="w-auto" action="">
                                <div class="input-group">
                                    <input type="text" id="id" class="form-control" name="IDorder" type="text" placeholder="Search for an order by ID..."/>
                                    <div class="input-group-append">
                                        <button type="submit" id="reset_btn" class="btn btn-primary mx-2">Search</button>
                                    </div>
                                </div>
                            </form>
                        </div>
                     </div>



                    <c:if test="${not empty orders}">

                    <!-- Order Content Popup -->
                    <div class="modal fade" id="orderContentModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="orderContentModalLabel" aria-hidden="true">
                      <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h1 class="modal-title fs-5" id="orderContentModalLabel">Order Content</h1>
                          </div>
                          <div class="modal-body">
                            <table class="table">
                              <thead>
                                <tr>
                                  <th>Item</th>
                                  <th>Quantity</th>
                                  <th></th>
                                  <th>Price (per unit)</th>
                                </tr>
                              </thead>
                              <tbody id="orderContentTableBody"></tbody>
                            </table>
                            <div class="text-end pe-4">
                              <strong>Total: </strong><span id="totalValue"></span>
                            </div>
                          </div>
                          <div class="modal-footer">
                            <div class="d-flex justify-content-between align-items-center flex-fill">
                              <div>
                                <form class="d-inline-block editBtn" method="GET" action="<c:url value='menu/mod'/>">
                                  <input type="hidden" name="order" id="modOrderField" value="" />
                                  <input type="hidden" name="sagra" id="modSagraField" value="" />
                                  <button class="btn btn-outline-secondary btn-sm" type="submit">Edit</button>
                                </form>
                                <form class="d-inline-block" method="GET" action="<c:url value='orders/delete'/>">
                                  <input type="hidden" name="IDorder" id="deleteOrderField" value="" />
                                  <button class="btn btn-outline-danger btn-sm" type="submit">Delete</button>
                                </form>
                              </div>
                              <div>
                                <form class="payForm" method="POST" action="" data-action="<c:url value='/'/>">
                                  <input type="hidden" name="IDorder" id="orderIDField" value="" />
                                  <button class="btn btn-primary " type="submit" id="payUnpayBtn"></button>
                                </form>
                              </div>
                            </div>
                          </div>

                        </div>
                      </div>
                    </div>


                    <!-- Order INFO Popup -->
                    <div class="modal fade" id="orderDetailsModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="orderDetailsModalLabel" aria-hidden="true">
                      <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h1 class="modal-title fs-5" id="orderDetailsModalLabel">Order Details</h1>
                          </div>
                          <div class="modal-body">
                            <table class="table">
                              <tbody>
                                <tr>
                                  <th>Customer Name:</th>
                                  <td id="clientName"></td>
                                </tr>
                                <tr>
                                  <th>Email:</th>
                                  <td id="email"></td>
                                </tr>
                                <tr>
                                  <th>Number of Customers:</th>
                                  <td id="numberOfClients"></td>
                                </tr>
                                <tr>
                                  <th>Number of Table:</th>
                                  <td id="numberOfTable"></td>
                                </tr>
                                <tr>
                                  <th>Cashier:</th>
                                  <td id="cashier"></td>
                                </tr>
                                <tr>
                                  <th>Order Time:</th>
                                  <td id="orderTime"></td>
                                </tr>
                                <tr>
                                  <th>Payment Time:</th>
                                  <td id="paymentTime"></td>
                                </tr>
                              </tbody>
                            </table>
                          </div>
                        </div>
                      </div>
                    </div>





                    <div class="container-fluid px-2 mx-2">
                      <div class="row justify-content-end">
                        <div class="text-right colSort">
                          <form id="sortForm" class="form-inline" method="get" action="">
                            <div class="form-group row">
                              <label class="col-auto ms-auto col-form-label text-end" for="sort">Order by:</label>
                              <div class="col-auto me-2">
                                <select name="sort" id="sort" onchange="submitSortForm()" class="form-control custom-select">
                                  <c:if test="${empty sorted}">
                                    <option value="">Newest Time</option>
                                    <option value="ID">ID</option>
                                    <option value="REVERSE_ID">Reverse ID</option>
                                    <option value="OLDEST_TIME">Oldest Time</option>
                                  </c:if>

                                  <c:if test="${not empty sorted}">
                                       <c:if test="${sorted eq 'ID'}">
                                           <option value="ID">ID</option>
                                       </c:if>
                                       <c:if test="${sorted eq 'REVERSE_ID'}">
                                           <option value="REVERSE_ID">Reverse ID</option>
                                       </c:if>
                                       <c:if test="${sorted eq 'OLDEST_TIME'}">
                                           <option value="OLDEST_TIME">Oldest Time</option>
                                       </c:if>

                                       <c:if test="${sorted ne 'ID'}">
                                            <option value="ID">ID</option>
                                       </c:if>
                                       <c:if test="${sorted ne 'REVERSE_ID'}">
                                          <option value="REVERSE_ID">Reverse ID</option>
                                       </c:if>
                                       <c:if test="${sorted ne 'OLDEST_TIME'}">
                                          <option value="OLDEST_TIME">Oldest Time</option>
                                       </c:if>
                                      <option value="">Newest Time</option>
                                  </c:if>
                                </select>
                               </div>
                              <input type="submit" style="display: none;">
                            </div>
                          </form>
                        </div>
                      </div>
                    </div>

                    <script>
                      function submitSortForm() {
                        document.getElementById("sortForm").submit();
                      }
                    </script>


                    <div class="table-responsive m-3 p-4 mt-1 pt-1">
                        <table class="table align-middle m-0 p-0 bg-light" style="overflow-x: auto;">
                            <thead class="bg-light">
                                <tr>
                                    <th>ID:</th>
                                    <th>Customer Name:</th>
                                    <th class="info_big">Email:</th>
                                    <th class="row_infobtn">Info:</th>
                                    <th class="info_big">Number of<br> Clients:</th>
                                    <th class="info_big">Number of<br> Table:</th>
                                    <th class="info_big">Cashier:</th>
                                    <th class="info_big">Order time:</th>
                                    <th class="info_big">Payment Time:</th>
                                    <th>Order Content:</th>
                                    <th>Total:</th>
                                    <th>Operation:</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="order" items="${orders}">
                                    <tr class="rounded mb-1 bg-white">
                                        <td><c:out value="${order.id}"/></td>
                                        <td><c:out value="${order.clientName}"/></td>
                                        <td class="info_big"><c:out value="${order.email}"/></td>
                                        <td class="row_infobtn">
                                            <button class="infoButton btn btn-outline-secondary"
                                                    clientName="${order.clientName}"
                                                    email="${order.email}"
                                                    numberOfClients="${order.clientNum}"
                                                    numberOfTable="${order.tableNumber}"
                                                    cashier="${order.idUser}"
                                                    orderTime="${order.orderTime}"
                                                    paymentTime="${order.paymentTime}"
                                                    >
                                                Info
                                            </button>
                                        </td>
                                        <td class="info_big"><c:out value="${order.clientNum}"/></td>
                                        <td class="info_big"><c:out value="${order.tableNumber}"/></td>
                                        <td class="info_big">
                                            <c:choose>
                                                <c:when test="${order.idUser > 0}">
                                                   <c:out value="${order.idUser}" />
                                                </c:when>
                                                <c:otherwise>
                                                   <c:out value="Unpaid" />
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="timestamp info_big">
                                            <c:choose>
                                                <c:when test="${empty order.orderTime}">
                                                   <c:out value="-" />
                                                </c:when>
                                                <c:otherwise>
                                                   <c:out value="${order.orderTime}" />
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="timestamp info_big">
                                            <c:choose>
                                                <c:when test="${empty order.paymentTime}">
                                                    <c:out value="-" />
                                                </c:when>
                                                <c:otherwise>
                                                    <c:out value="${order.paymentTime}" />
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <button class="contentButton btn btn-outline-secondary"
                                                    data-id="${order.id}"
                                                    sagra="${order.orderContent[0].idSagra}"
                                                    base_URL="<c:url value='/'/>"
                                                    payed="${order.idUser > 0}"
                                                    >
                                                Order Content
                                            </button>

                                            <!--
                                            <table class="table table-responsive" style="width: 100%;">
                                                <thead class="bg-light">
                                                  <tr>
                                                    <th>Order Name</th>
                                                    <th>Quantity</th>
                                                    <th>Price</th>
                                                  </tr>
                                                </thead>
                                                <tbody>
                                                  <c:forEach var="content" items="${order.orderContent}">
                                                    <tr>
                                                      <td><c:out value="${content.productName}" /></td>
                                                      <td><c:out value="${content.quantity}" /></td>
                                                      <td><c:out value="${content.price}" /> €</td>
                                                    </tr>
                                                  </c:forEach>
                                                </tbody>
                                            </table>
                                            -->
                                        </td>
                                        <td  id="totalTD"
                                             data-id="${order.id}"
                                             sagra="${order.orderContent[0].idSagra}"
                                             base_URL="<c:url value='/'/>">
                                        -
                                        </td>

                                        <td>
                                              <div class="col-3 d-flex flex-column m-0 p-0">
                                                <form class="flex-fill" method="POST" action="<c:url value='${(order.idUser > 0) ? "/payedorders/unpay" : "/orders/pay"}'/>">
                                                  <input type="hidden" name="IDorder" value="${order.id}" />
                                                  <button class="btn btn-primary flex-fill mb-1" type="submit">${(order.idUser > 0) ? 'UNPAY' : 'PAY'}</button>
                                                </form>
                                                <c:if test="${order.idUser < 1}">
                                                  <form class="editBtn flex-fill " method="GET" action="<c:url value='menu/mod'/>">
                                                    <input type="hidden" name="order" value="${order.id}" />
                                                    <input type="hidden" name="sagra" value="${order.orderContent[0].idSagra}" />
                                                    <button class="btn btn-outline-secondary flex-fill mb-1 btn-sm editBtn" type="submit">Edit</button>
                                                  </form>
                                                </c:if>
                                                <form class="flex-fill" method="GET" action="<c:url value='orders/delete'/>">
                                                  <input type="hidden" name="IDorder" value="${order.id}" />
                                                  <button class="btn btn-outline-danger flex-fill mb-1 btn-sm" type="submit">Delete</button>
                                                </form>
                                              </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    </c:if>
                    <c:if test="${empty orders}">
                      <h3 class=" p-5 m-4 text-center me-auto emptyMessage">There are no orders</h3>
                    </c:if>

                </div>

            </body>

        </c:otherwise>
    </c:choose>

    <c:import url="/jsp/footers/footer.jsp"/>

</html>