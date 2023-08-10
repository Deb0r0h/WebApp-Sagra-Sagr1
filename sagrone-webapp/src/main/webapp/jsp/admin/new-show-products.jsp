<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>



<!DOCTYPE html>
<html lang="en">
    <c:choose>
        <c:when test="${empty sessionScope.admin}">
            <head>
                <title>Unauthorized access - You should not be here</title>
                <!-- FAVICON -->
                <c:import url="/jsp/headers/common-header-tags.jsp"/>
            </head>
            <body>
                <h1>UNAUTHORIZED ACCESS</h1>
                <p>You should not be here UNKNOWN USER</p>
            </body>
        </c:when>
        <c:otherwise>
            <head>
                <title>Products</title>

                <meta name="viewport" content="width=device-width, initial-scale=1">


                <c:import url="/jsp/include/bootstrap.jsp"/>
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.js"></script>

                <link href="<c:url value="/css/products.css"/>"  type="text/css" rel="stylesheet"/>
                <link href="<c:url value="/css/base.css"/>" type="text/css" rel="stylesheet"/>
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">

                <!-- FAVICON -->
                <c:import url="/jsp/headers/common-header-tags.jsp"/>
            </head>
            <body>

                <!-- display the message -->


                <c:import url="/jsp/headers/header.jsp"/>

                <c:if test="${message.error}">
                    <div class="alert alert-danger" role="alert">${message.message}</div>
                </c:if>

                <br/>

                <div id="insert" class="container col-md-8 my-3">
                    <button id="insert-prod-button" type="button" class="mt-auto btn btn-primary" data-toggle="modal" data-target="#myModal">Insert a new product</button>
                </div>

                <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="myModalLabel">Product info</h5>
                                <button type="button" class="btn btn-secondary" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div class="modal-body">
                                <jsp:include page="insert-product-form.jsp" />
                            </div>
                        </div>
                    </div>
                </div>

                <br>
             <div class="container">
                <div class="row justify-content-md-center">
                    <div class="col-md-8 mb-4">
                        <div class="input-group">
                            <input type="text" id="search_input" class="form-control" placeholder="Search for a product name..."/>
                            <div class="input-group-append">
                                <button id="reset_btn" class="mt-auto btn btn-secondary">Reset</button>
                            </div>
                        </div>
                    </div>
                 </div>

                <c:if test="${not empty productList}">
                    <div class="row">
                        <c:forEach var="product" items="${productList}">
                           <div class="col-md-4 col-sm-6 col-12 mb-4">
                                <div  class="card">
                                    <div class="card-header">
                                        <h5 class="card-title">${product.name}</h5>
                                    </div>

                                    <c:choose>
                                        <c:when test="${product.hasPhoto()}">
                                            <img class="card-img-top" src="<c:url value="/load-product-photo/"><c:param name="sagra" value="${product.idSagra}"/><c:param name="name" value="${product.name}"/></c:url>" alt="Product image"/>
                                        </c:when>
                                        <c:otherwise>
                                            <img class="card-img-top" src="<c:url value="/media/no-image.png"/>" alt="Placeholder image"/>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="card-body">



                                        <p class="card-text"><span>Description:</span> ${product.description}</p>
                                        <p class="card-text"><span>Price:</span>${String.format(" %.2f",product.price)}</p>
                                        <p class="card-text"><span>Category:</span> ${product.category}</p>
                                        <p class="card-text"><span>Bar: </span>${product.bar ? 'Yes' : 'No'}</p>
                                        <p class="card-text"><span>Available:</span> ${product.available ? 'Yes' : 'No'}</p>
                                    </div>
                                    <div class="card-footer">
                                        <button id="modifyBut" type="button" onclick="openPopup('${product.name}', '${product.description}', '${String.format("%.2f",product.price)}', '${product.bar}', '${product.available}', '${product.category}')" class="mt-auto btn btn-primary" data-toggle="modal" data-target="#UpdateModal">Modify</button>

										<div class="modal fade" id="UpdateModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">

                                            <div class="modal-dialog" role="document">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h5 class="modal-title" id="modal">Product info</h5>
                                                        <button type="button" class="btn btn-secondary" data-dismiss="modal" aria-label="Close">
                                                            <span aria-hidden="true">&times;</span>
                                                        </button>
                                                    </div>
                                                    <div class="modal-body">
                                                          <jsp:include page="new-update-product-form.jsp" />
                                                    </div>

                                                </div>
                                            </div>
                                        </div>


                                    <!-- Add a unique ID to the delete button and its form -->
                                   <button id="deleteBtn_${product.name}" type="button" class="mt-auto btn btn-danger" data-toggle="modal" data-target="#deleteModal_${product.name}">Delete</button>
                                   <form id="deleteForm_${product.name}" method="POST" enctype="multipart/form-data" action="<c:url value="/seeprod/delete"/>" style="display:none;">
                                       <input type="hidden" id="Oldname" name="Oldname" value="${product.name}"/>
                                   </form>


                                   <div class="modal fade" id="deleteModal_${product.name}" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel_${product.name}" aria-hidden="true">
                                       <div class="modal-dialog" role="document">
                                           <div class="modal-content">
                                               <div class="modal-header">
                                                   <h5 class="modal-title" id="deleteModalLabel_${product.name}">Confirm deletion</h5>
                                                   <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                       <span aria-hidden="true">&times;</span>
                                                   </button>
                                               </div>
                                               <div class="modal-body">
                                                   Are you sure you want to delete this product?
                                               </div>
                                               <div class="modal-footer">
                                                   <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                                                   <button type="button" class="btn btn-danger" onclick="document.getElementById('deleteForm_${product.name}').submit();">Confirm</button>
                                               </div>
                                           </div>
                                       </div>
                                   </div>
                                    </div>
                                </div>

                           </div>
                        </c:forEach>
                    </div>
                </c:if>
                <c:if test="${empty productList}">
                    <!-- message show?-->
                    <p>No products found.</p>
                </c:if>
         </div>

          <button id="btn_scroll-top" class="btn btn-primary">
                 <i class="fas fa-arrow-up"></i>
          </button>



           <c:import url="/jsp/footers/footer.jsp"/>
           <script src="<c:url value="/js/products.js"/>"></script>
            </body>
        </c:otherwise>
    </c:choose>
</html>
