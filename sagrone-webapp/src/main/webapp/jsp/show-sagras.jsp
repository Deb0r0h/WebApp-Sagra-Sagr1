<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
	<head>
	    <title>Sagrone</title>

	    <meta name="viewport" content="width=device-width, initial-scale=1">

        <!-- BOOTSTRAP -->
	    <c:import url="/jsp/include/bootstrap.jsp"/>
        <!-- CSS -->
        <link href="<c:url value="/css/base.css"/>"  type="text/css" rel="stylesheet"/>
        <link href="<c:url value="/css/show-sagras.css"/>"  type="text/css" rel="stylesheet"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">

        <!-- scripts-->
        <script src="<c:url value="/js/message.js"/>"></script>
        <!-- FAVICON -->
        <c:import url="/jsp/headers/common-header-tags.jsp"/>
	</head>

	<body>


        <div class="background-image" style="background-image: url('<c:url value="/media/backgroundLogo.jpg"/>');">
        <img src="<c:url value="/media/sagrone-logo.png"/>" alt="sagrone" id="main-logo">
        </div>
        <c:import url="/jsp/headers/header.jsp"/>

        <c:if test="${message.error}">
            <c:import url="/jsp/include/show-message.jsp"/>
        </c:if>


        <br/>

        <div class="container">
            <div class="row justify-content-md-center">
                <div class="col-md-8 mb-4">
                    <div class="input-group">
                        <input type="text" id="search_input" class="form-control" placeholder="Search for a sagra..."/>
                        <div class="input-group-append">
                            <button id="reset_btn" class="btn btn-secondary">Reset</button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <c:if test="${not empty sagrasList}">
                    <c:forEach var="sagra" items="${sagrasList}">
                        <div class="col-md-4 col-sm-6 col-12 mb-4">
                            <div class="card">
                                <div class="card-body d-flex flex-column">
                                    <h5 class="card-title"><c:out value="${sagra.name}"/></h5>
                                    <h6 class="card-address"><c:out value="${sagra.address}"/></h6>
                                    <p class="card-text"><c:out value="${sagra.description}"/></p>
                                    <a href="menu/?sagra=${sagra.id}" class="mt-auto btn btn-primary" role="button"><i class="fas fa-utensils"></i>  Order</a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:if>
            </div>
        </div>

    <button id="btn_scroll-top" class="btn btn-primary">
        <i class="fas fa-arrow-up"></i>
    </button>

    <c:import url="/jsp/footers/footer.jsp"/>

    <!-- JS -->
    <script src="<c:url value="/js/show-sagras.js"/>"></script>

	</body>
</html>