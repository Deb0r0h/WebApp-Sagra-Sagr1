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
