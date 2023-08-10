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
<html>
<head>
<title>test order pdf print</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
 <center>
  <h1>Click on Below Link to Get your PDF</h1>
 </center>
 <center>
  <a href="<c:url value="/DownloadSummaryPDF"><c:param name="sagra" value="3"/><c:param name="order" value="1"/></c:url>">Click Here for summary</a>
  </br>
  <a href="<c:url value="/DownloadCashierPDF"><c:param name="sagra" value="3"/><c:param name="order" value="1"/></c:url>">Click Here cashier PROTECTED</a>
 </center>
</body>
</html>