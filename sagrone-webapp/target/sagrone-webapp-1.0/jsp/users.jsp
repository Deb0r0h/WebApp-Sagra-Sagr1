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

 Author: Diego Spinosa (diego.spinosa@studenti.unipd.it)
 Version: 1.0
 Since: 1.0
-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>


<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>TODO: METTERE IL NOME DELLA SAGRA</title>

</head>
<body>

<div>
    <p>Pagina di prova: lista utenti della sagra</p>

    <div style="padding: 3%">
        <div>
            <h4>Users List</h4>

            <c:import url="/jsp/include/user-status.jsp"/>

            <c:if test="${empty param.edit}">
               <table id="members_table">

                   <tr>
                       <th >id</th>
                       <th >Username</th>
                       <th >Password</th>
                   </tr>
                   <c:forEach items="${userList}"  var="u" varStatus="loop">
                       <tr>
                           <td >${u.getId()}</td>
                           <td >${u.getUsername()}</td>
                           <td >${u.getPassword()}</td>
                       </tr>
                   </c:forEach>
               </table>
               <a href="users?edit=1">Edit</a>
            </c:if>

            <c:if test="${!empty param.edit}">
               <form method="POST" action="users/update">
                   <table id="members_table">

                       <tr>
                           <th >id</th>
                           <th >Username</th>
                           <th >Password</th>
                       </tr>
                       <c:forEach items="${userList}"  var="u" varStatus="loop">
                           <tr>
                               <td >${u.getId()}</td>
                               <td><input type="text" name="${u.getId()}" value="${u.getUsername()}" ></td>
                               <td><input type="text" name="${u.getId()}" placeholder="Unchanged" ></td>
                               <td><input type="submit" value="Delete" formmethod="POST" formaction="users/delete?del=${u.getId()}" /></td>
                           </tr>
                       </c:forEach>
                   </table>
                   <input type="submit" value="Save changes" />
               </form>


               <p><br/><br/>Add new users:</p>
               <form method="POST" action="users/create">
                                  <table id="adduser_table">

                                      <tr>
                                          <th >Username</th>
                                          <th >Password</th>
                                      </tr>
                                      <tr>
                                          <td><input type="text" name="newUsername" placeholder="New username" ></td>
                                          <td><input type="text" name="newPassword" placeholder="New password" ></td>
                                      </tr>
                                  </table>
                                  <input type="submit" value="Add user" />
                              </form>
            </c:if>


            <p>Serves messages: </br>
            <c:forEach items="${messages}"  var="m" varStatus="loop">
                ${m.getMessage()} </br>
            </c:forEach>
            </p>
        </div>
    </div>
</div>
</body>
</html>