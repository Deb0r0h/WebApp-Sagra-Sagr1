<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:choose>
        <c:when test="${empty sessionScope.cashier}">
           <c:import url="/jsp/nav/user-navbar.jsp"/>
        </c:when>

        <c:otherwise>

        <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top" id="navbar">
          <a class="navbar-brand" href="<c:url value="/"/>">
                <img src="<c:url value="/media/sagrone-logo.png"/>" alt="sagrone"  id="navbar-logo">
          </a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarTogglerSagrone" aria-controls="navbarTogglerSagrone" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
          </button>

          <div class="collapse navbar-collapse" id="navbarTogglerSagrone">
            <ul class="navbar-nav me-auto">
                  <li class="nav-item mt-auto mb-auto">
                      <a class="nav-link" id="paidOrdersLink" href="<c:url value="/payedorders"/>">Paid Orders</a>
                    </li>
                  <li class="nav-item mt-auto mb-auto">
                       <a class="nav-link" id="ordersLink" href="<c:url value="/orders"/>">Orders</a>
                  </li>
                  <li class="nav-item mt-auto mb-auto">
                       <a class="nav-link" id="menuLink" href="<c:url value="/menu/"><c:param name="sagra" value="${sessionScope.sagra}"/></c:url>">New order</a>
                  </li>
            </ul>
            <ul class="navbar-nav ms-auto mt-auto">
                  <li class="nav-item">
                    <a class="nav-link text-primary" href="<c:url value="/logout"/>">Logout</a>
                  </li>
            </ul>
          </div>
        </nav>


          <!--
          <nav class="navbar navbar-expand-lg navbar-light bg-light ">
            <div class="container">
              <a class="navbar-brand" href="<c:url value="/"/>">
                <img src="<c:url value="/media/sagrone-logo.png"/>" alt="sagrone" width="5%">
              </a>
              
              <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav ms-auto">
                  <li class="nav-item">
                      <c:choose>
                          <c:when test="${not empty navbarTitle}">
                            <h1 class="text-primary">"${param.Link}"</h1>
                          </c:when>
                          <c:otherwise>
                            <a class="nav-link" href="<c:url value="/payedorders"/>">"Orders -${param.Link}"</a>
                          </c:otherwise>
                    </c:choose>
                    <a class="nav-link" href="<c:url value="/payedorders"/>">Orders</a>
                  </li>
                  <li class="nav-item">
                    <a class="nav-link" href="<c:url value="/orders"/>">Unpaid orders</a>
                  </li>
                  <li class="nav-item">
                    <a class="nav-link" href="<c:url value="/menu/"><c:param name="sagra" value="${sessionScope.sagra}"/></c:url>">New order</a>
                  </li>
                </ul>
              </div>
            </div>
          </nav>
        -->
        </c:otherwise>
</c:choose>