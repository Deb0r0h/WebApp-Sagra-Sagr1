<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>



        <nav class="navbar navbar-expand-lg navbar-dark bg-dark" id="navbar">
          <a class="navbar-brand" href="<c:url value="/"/>">
                <img src="<c:url value="/media/sagrone-logo.png"/>" alt="sagrone" id="navbar-logo">
          </a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarTogglerSagrone" aria-controls="navbarTogglerSagrone" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
          </button>

          <div class="collapse navbar-collapse" id="navbarTogglerSagrone">
            <ul class="navbar-nav ms-auto">
                  <li class="nav-item">
                    <a class="nav-link text-primary" href="<c:url value="/login"/>">Login</a>
                  </li>
            </ul>
          </div>
        </nav>
