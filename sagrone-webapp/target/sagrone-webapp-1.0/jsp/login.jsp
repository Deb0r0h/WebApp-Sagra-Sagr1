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

 Author: Gianluca Rossi (gianluca.rossi.4@studenti.unipd.it)
 Version: 1.0
 Since: 1.0
-->

<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>

<html lang="en">
    <head>
    	<title>Login page</title>

        <!-- Adding bootstrap -->
        <c:import url="/jsp/include/bootstrap.jsp"/>

        <!-- CSS -->
        <link href="<c:url value="/css/base.css"/>"  type="text/css" rel="stylesheet"/>
        <link href="<c:url value="/css/login.css"/>"  type="text/css" rel="stylesheet"/>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <script src="<c:url value="/js/message.js"/>"></script>

        <!-- FAVICON -->
        <c:import url="/jsp/headers/common-header-tags.jsp"/>

    </head>

    <body>

        <div class="background-image" style="background-image: url('<c:url value="/media/backgroundLogo.jpg"/>');">
            <img src="<c:url value="/media/sagrone-logo.png"/>" alt="sagrone" id="main-logo">
        </div>

        <c:import url="/jsp/headers/header.jsp"/>

        <div class="row justify-content-center">
            <div class="col-lg-5 col-md-6 col-sm-9 col-xs-11">
                <div class="card bg-light p-4 m-5 ">
                    <form method="POST" action="<c:url value="/login/"/>">
                        <div class="form-outline mb-3">
                            <label class="head" for="usr">Username:</label>
                            <input name="username" id="usr" required placeholder="Type your username here" class="form-control form-control-lg" maxlength="20" />
                        </div>
                        <div class="form-outline mb-3">
                            <label class="head" for="pwd">Password:</label>
                            <input type="password" name="password" id="pwd" required placeholder="Type your password here"  class="form-control form-control-lg" maxlength="64" />
                        </div>
                        <button type="submit" name="button" class="container btn btn-primary btn-lg btn-block">Login</button>
                    </form>
                </div>
                <div class="error head">
                    <c:if test="${message.error}">
                        <c:import url="/jsp/include/show-message.jsp"/>
                    </c:if>
                </div>
            </div>
        </div>
    </body>
</html>
