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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<footer class="bg-dark text-white text-center text-lg-start " id="footer">
    <!-- Grid container -->
    <div class="container p-4">

        <div class="row">

            <div class="col-lg-6 col-md-12 mb-4 mb-md-0">
                <h5 class="text-uppercase">SAGRONE</h5>

                <p>
                    Sagrone offers you a platform to place order for your favorite SAGRA! contact us if you want to add your sagra!
                </p>
            </div>

            <div class="col-lg-3 col-md-6 mb-4 mb-md-0">
                <h5 class="text-uppercase">Our site</h5>

                <ul class="list-unstyled mb-0">
                    <li>
                        <a href="<c:url value="/login"/>" class="text-white">Login</a>
                    </li>
                    <li>
                        <a href="<c:url value="/"/>" class="text-white">Order</a>
                    </li>
                </ul>
            </div>

            <div class="col-lg-3 col-md-6 mb-4 mb-md-0">
                <h5 class="text-uppercase mb-0">Contacts</h5>

                <address>
                    info.sagrone@gmail.com
                </address>
            </div>

        </div>

    </div>

    <div class="text-center p-3" style="background-color: rgba(0, 0, 0, 0.2)">
        © 2023 Copyright: Sagrone
    </div>
    <!-- Copyright -->
</footer>