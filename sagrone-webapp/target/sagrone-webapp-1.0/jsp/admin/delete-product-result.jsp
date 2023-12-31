<!--
Copyright 2023 University of Padua, Italy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Author: Matteo Carpentieri (matteo.carpentieri@studenti.unipd.it)
Version: 1.0
Since: 1.0
-->

<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">

    <c:choose>
            <c:when test="${empty sessionScope.admin}">
                <head>
                    <title> Unauthorized acces - You should not be here</title>
                    <!-- FAVICON -->
                    <c:import url="/jsp/headers/common-header-tags.jsp"/>
                </head>

                <body>
                <h1> UNAUTHORIZED ACCESS</h1>

                <p>You should not be here UNKNOWN USER</p>

                </body>

            </c:when>

            <c:otherwise>
               <head>
               	<title>Delete Product</title>
                   <!-- FAVICON -->
                   <c:import url="/jsp/headers/common-header-tags.jsp"/>
               </head>

               <body>
               <h1>Delete Product</h1>
               <hr/>
               <!-- display the message -->
               <c:import url="/jsp/include/show-message.jsp"/>
               <!-- display the just updated product, if any and no errors -->
               <c:if test="${not empty product && !message.error}">
               	<ul>
               		<li>name: <c:out value="${product.name}"/></li>
               		<li>id_sagra: <c:out value="${product.idSagra}"/></li>
               	</ul>
               </c:if>
               </body>
            </c:otherwise>
        </c:choose>

</html>