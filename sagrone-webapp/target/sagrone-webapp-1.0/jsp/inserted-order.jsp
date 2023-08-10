<%--
  User: Riccardo Gobbo
  Email: riccardo.gobbo.2@studenti.unipd.it
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">

        <!-- Adding bootstrap -->
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"/>
        <link href="https://getbootstrap.com/docs/5.3/assets/css/docs.css" rel="stylesheet" />
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>

        <!-- CSS -->
        <link href="<c:url value="/css/base.css"/>"  type="text/css" rel="stylesheet"/>
        <link href="<c:url value="/css/inserted-order.css"/>" rel="stylesheet"/>

        <c:when test="${message.error}">
            <title>Order NOT Sent!</title>
            <script src="<c:url value="/js/inserted-order.js"/>"></script>
        </c:when>
        <c:otherwise>
            <title>Order Sent!</title>
        </c:otherwise>

        <!-- FAVICON -->
        <c:import url="/jsp/headers/common-header-tags.jsp"/>

    </head>
    <c:choose>

        <c:when test="${message.error}">
            <body>
                <p class="general" style="font-weight:bold" > We are sorry, an error occured while managing your request </p>

                <c:import url="/jsp/include/show-message.jsp"/>
                <p> Please retry </p>
            </body>
        </c:when>

        <c:otherwise>

            <body class="p-3 m-3 border-0">
                <p class="border-top border-bottom text-center col-12 form-control border-success bg-success-subtle" >
                    <c:out value="${message.message}"/>
                </p>

                <div id="global_wrapper" class="card text-center" >

                    <div class="container text-center">
                        <div class="row justify-content-center">
                            <div class="col-2"></div>
                            <div class="col-7">
                                <div class="bg-primary bg-gradient container text-center border rounded-3" >
                                    <div id="order_id">
                                        <c:out value="${generated_order_id}"/>
                                    </div>
                                </div>
                            </div>
                            <div class="col-2"></div>
                        </div>
                    </div>

                    <div class="row justify-content-center">
                        <div class="col-2"></div>
                        <div class="col-11">
                            <div class="container text-center">
                                <div class="">
                                    <div class="card-body">
                                        <span id="total">TOTAL: <c:out value="${total_price}"/>€</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-2"></div>
                    </div>

                    <div class="row justify-content-center">
                        <div class="col-10">
                            <div class=" text-center">
                                <div>
                                    <form action="<c:url value="/DownloadSummaryPDF"/>" method="GET">
                                        <input
                                                id="download_pdf"
                                                type="submit"
                                                class="btn btn-primary"
                                                value="DOWNLOAD PDF"
                                        />
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>


            </body>
        </c:otherwise>

    </c:choose>



</html>

