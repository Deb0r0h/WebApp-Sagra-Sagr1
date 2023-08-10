<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link href="<c:url value="/css/message.css"/>"  type="text/css" rel="stylesheet"/>
<c:choose>
	<c:when test="${message.error}">
	    <div id="message" class="alert alert-danger sagrone-message">
            <span ><c:out value="${message.message}"/></span>
        </div>
	</c:when>

	<c:otherwise>
        <div id="message" class="alert alert-success sagrone-message">
		    <span ><c:out value="${message.message}"/></span>
		</div>
	</c:otherwise>
</c:choose>