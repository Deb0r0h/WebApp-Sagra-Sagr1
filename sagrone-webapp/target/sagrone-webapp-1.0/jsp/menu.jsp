<!--
 Copyright 2018-2023 University of Padua, Italy

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Author: Simone Merlo (simone.merlo@studenti.unipd.it)
 Version: 1.0
 Since: 1.0
-->

<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE html>
<html lang="en">
	<head>
		<title>Menu</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
		<!-- Adding bootstrap -->
		<c:import url="/jsp/include/bootstrap.jsp"/>
		<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>

        <!-- CSS -->
        <link href="<c:url value="/css/base.css"/>"  type="text/css" rel="stylesheet"/>
        <link href="<c:url value="/css/menu.css"/>"  type="text/css" rel="stylesheet"/>
        <link href="<c:url value="/css/inserted-order.css"/>"  type="text/css" rel="stylesheet"/>

        <!-- JS -->
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="<c:url value="/js/menu.js"/>"></script>

        <!-- FAVICON -->
        <c:import url="/jsp/headers/common-header-tags.jsp"/>

	</head>

	<body class="bg-light">






    <div class="background-image" style="background-image: url('<c:url value="/media/backgroundLogo.jpg"/>');">
        <img src="<c:url value="/media/sagrone-logo.png"/>" alt="sagrone" id="main-logo">
    </div>

       <c:import url="/jsp/headers/header.jsp"/>


       <!-- display the message -->
        <div id="message">
            <c:if test="${message.error}">
                <c:import url="/jsp/include/show-message.jsp"/>
            </c:if>
        </div>

    <div class="alert alert-danger" role="alert" id="error-alert"></div>


        <!-- display the list of products-->
        <div id="full-container">
                    <nav class="navbar navbar-expand navbar-light bg-light" id="categories-nav">
                        <div class="container-fluid">
                            <div class="collapse navbar-collapse" >
                                <div class="navbar-nav" id="categories-list">

                                </div>

                            </div>
                        </div>
                    </nav>

                <div id="cart-buttons">
                    <div class="col">
                        <button type="button" class="btn btn-success" data-toggle="modal" data-target="#cart">
                            <i class="bi bi-cart4"></i>
                            (<span id="total-counter" class="total-count"></span>)
                        </button>
                        <button class="clear-cart btn btn-danger">
                            <i class="bi bi-cart-x-fill"></i>
                        </button>
                    </div>
                </div>
        <!-- products -->
            <div class = "container-text-center" id = "products-container">
            </div>
        </div>

        <div id="global_wrapper" class="card text-center">
        <div class="row justify-content-center">
            <div class="col-2"></div>
            <div class="col-11">
                <div class="container text-center">
                    <div class="">
                        <div class="card-body text-center">
                            <span id="total">Your ORDER NUMBER: </span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-2"></div>
        </div>
        <div class="container text-center">
            <div class="row justify-content-center">
                <div class="col-2"></div>
                <div class="col-7">
                    <div class="bg-primary bg-gradient container text-center border rounded-3">
                        <div id="order_id">
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
                        <div class="card-body text-center">
                            <span id="totalCost">TOTAL: <span id="totalValue"></span> €</span>
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
                        <button id="download_pdf" class="btn btn-primary"> Download order recap PDF </button>
                    </div>
                </div>
            </div>
        </div>
        <div class="row justify-content-center">
            <div class="col-12">
                <div class="container ">
                    <div class="card-body text-center">
                        <p>Now go to a cash desk in order to pay for your order and we will start preparing it.</p>
                    </div>
                </div>
            </div>
        </div>
    </div>

        <!-- Item popup -->
        <div class="modal fade" id="staticBackdrop" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h1 class="modal-title fs-5" id="staticBackdropLabel">dish-name</h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close" id="itemclose"></button>
                    </div>
                    <div class="modal-body" id="dishDescription">
                        dish-description
                    </div>
                    <div class="btn-group" role="group" aria-label="Basic mixed styles example">
                        Quantity :
                        <span id = "quantity">1</span>
                        <button type="button" id="minusbut" class="btn btn-danger"><i class="bi bi-dash-circle"></i></button>
                        <button type="button" id="plusbut" class="btn btn-success"><i class="bi bi-plus-circle"></i></button>
                    </div>
                    <div class="modal-footer">
                        <button type="button" id = "closebut" class="btn btn-secondary" data-bs-dismiss="modal"><i class="bi bi-x-square-fill"></i> Close</button>
                        <button type="button" id = "addbut" class="add-to-cart btn btn-warning" data-bs-dismiss="modal"><i class="bi bi-cart-plus-fill"></i> Add to cart</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- cart -->
        <div class="modal fade" id="cart" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg" role="document" id="cart2">
                <div class="modal-content" id="cart-container">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLabel">Cart</h5>
                        <button type="button" class="btn-close" data-dismiss="modal" aria-label="Close" id="cartclose">
                        </button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label for="nameInput">Name:</label>
                            <input type="text" class="form-control" id="nameInput" placeholder="Enter your name">
                        </div>
                        <div class="form-group">
                            <label for="tableInput">Table number:</label>
                            <input type="text" class="form-control" id="tableInput" placeholder="Enter your table number">
                        </div>
                        <div class="form-group">
                            <label for="seatsInput">Number of seats:</label>
                            <input type="number" class="form-control" id="seatsInput" placeholder="Enter the number of seats" min="1" step="1">
                        </div>
                        <div class="form-group">
                            <label for="emailInput">Email(optional):</label>
                            <input type="email" class="form-control" id="emailInput" placeholder="Optional: enter your email">
                        </div>
                        <div class="show-cart table">

                        </div>
                        <div>Total price: € <span class="total-cart"></span></div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal" id="close-cart">Close</button>
                        <button type="button" id="orderbut" class="btn btn-primary">Order now</button>
                    </div>
                </div>
            </div>
        </div>

        <c:import url="/jsp/footers/footer.jsp"/>
	</body>
</html>