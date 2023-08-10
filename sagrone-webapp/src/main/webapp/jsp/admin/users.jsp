<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>


<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="ISO-8859-1">
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
	<link href="<c:url value="/css/base.css"/>"  type="text/css" rel="stylesheet"/>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Users</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.4/jquery.min.js"></script>
    <!-- FAVICON -->
    <c:import url="/jsp/headers/common-header-tags.jsp"/>
</head>
<body>

<c:import url="/jsp/headers/header.jsp"/>

<div class="container text-center mt-3 col-lg-6" style="clear:both;">
    <c:forEach items="${messages}"  var="m" varStatus="loop">
        <c:if test="${m.isError()}">
            <div class="alert alert-danger">${m.getMessage()}</div>
        </c:if>
    </c:forEach>
	<div class="card">
	    <div class="card-header">
	        <div class="row align-items-center">
    		    <div class="col-2"><strong>ID</strong></div>
    		    <div class="col"><strong>Username</strong></div>
    		    <div class="col-3"><button class="btn btn-secondary" type="button" data-bs-toggle="collapse" data-bs-target="#collapseNewUser" aria-expanded="false" aria-controls="collapseNewUser">Add</button></div>
  	        </div>
  	        <div class="row align-items-center collapse" id="collapseNewUser">
               <form method="POST" action="users/create" class="needs-validation" autocomplete="off" novalidate >
                  <div class="row align-items-center mt-2">
                    <label class="col-auto col-form-label" for="userNew">Username: </label>
                    <div class="col">
                        <input class="form-control" id="userNew" type="text" name="newUsername" placeholder="New username" autocomplete="off" required />
                        <div class="invalid-feedback">Please insert a username</div>
                    </div>
                  </div>
                  <div class="row align-items-center mt-2">
                    <label class="col-auto col-form-label" for="pwNew">Password: </label>
                    <div class="col">
                        <input class="form-control ms-1" id="pwNew" type="password" name="newPassword" placeholder="New password" pattern="^(?=.*[A-Z])(?=.*[0-9]).{8,}$" autocomplete="new-password" required/>
                        <div class="invalid-feedback">Password must be at least 8 characters long, with at least a number and a capital letter</div>
                    </div>
                  </div>
                  <div class="row align-items-center mt-2">
                    <div class="col"><input class="btn btn-primary" type="submit" value="Save user" /></div>
                  </div>
              </form>
           </div>
  	    </div>

        <ul class="list-group list-group-flush">
            <c:forEach items="${userList}"  var="u" varStatus="loop">
               <li class="list-group-item">
                   <div class="row align-items-center">
                    <div class="col-2">${u.getId()}</div>
                    <div class="col">${u.getUsername()}</div>
                    <div class="col-3"><button class="btn btn-primary" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${u.getId()}" aria-expanded="false" aria-controls="collapse${u.getId()}">Edit</button></div>
                   </div>

                   <div class="row align-items-center collapse" id="collapse${u.getId()}">
                       <form method="POST" action="users/update" autocomplete="no" class="needs-validation" novalidate >
                          <div class="row align-items-center mt-2">
                            <label class="col-auto col-form-label" for="user_${u.getId()}">Username: </label>
                            <div class="col">
                                <input class="form-control" id="user_${u.getId()}" type="text" name="${u.getId()}" autocomplete="off" value="${u.getUsername()}" required/>
                                <div class="invalid-feedback">Please insert a username</div>
                            </div>
                          </div>
                          <div class="row align-items-center mt-2">
                            <label class="col-auto col-form-label" for="pw_${u.getId()}">Password: </label>
                            <div class="col">
                                <input class="form-control ms-1" id="pw_${u.getId()}" type="password" pattern="^(?=.*[A-Z])(?=.*[0-9]).{8,}$" name="${u.getId()}" autocomplete="new-password" placeholder="Unchanged"/>
                                <div class="invalid-feedback">Password must be at least 8 characters long, with at least a number and a capital letter</div>
                            </div>
                          </div>
                          <div class="row align-items-center mt-2">
                            <div class="col"><input class="btn btn-danger" type="submit" value="Delete" formmethod="POST" formaction="users/delete?del=${u.getId()}" /></div>
                            <div class="col-6"><input class="btn btn-primary" type="submit" value="Save changes" /></div>
                          </div>
                      </form>
                   </div>
               </li>
           </c:forEach>
        </ul>
    </div>
    <div class="text-secondary mt-3">In this page you can manage the cashiers of your sagra, editing their credentials or adding new ones!</div>
</div>


<c:import url="/jsp/footers/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
<script src="<c:url value="/js/users.js"/>"></script>
</body>
</html>