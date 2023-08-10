
//declaring shopping cart
var shoppingCart;

//defining url
var host= window.location.protocol+"//"+window.location.host+"/";
var sagronewebapp=window.location.href.replace(host,"");
//console.log(basicUrl);
//console.log(sagronewebapp);
sagronewebapp=sagronewebapp.slice(0,sagronewebapp.indexOf("/"));
//console.log(sagronewebapp);
var baseURL=host+sagronewebapp;

document.addEventListener("DOMContentLoaded", function(){
    var categoriesList;
    var product_list;
    let queryString=window.location.search;
    let params= new URLSearchParams(queryString);
    let url = baseURL+"/rest/categories/available/" + params.get('sagra'); //CHANGED FROM HTTPS!!
    var xhr= new XMLHttpRequest();
    xhr.open("GET", url);
    //xhr.setRequestHeader('Accept', 'application/json');
    xhr.onload=function () {
        if(xhr.status === 200) {
            var responseObject = JSON.parse(xhr.responseText);
            const categoryObjects = responseObject['resource-list'].map(item => item.category);
            categoriesList = categoryObjects.map(category => category.name);
            generate_categories_list(categoriesList, params.get('sagra'));

        }else{
            printError("Unable to load products!",20);
        }
    };
    xhr.send();
});

function generate_categories_list(categoriesList, id_sagra){
    var categoriesdiv=document.getElementById("categories-list");
    if(categoriesList.length > 0){
        let firstCategory; //needed to load the first elements.
        for(var i=0; i<categoriesList.length; i++){
            //var categoryli= document.createElement("li");
            //categoryli.className = "nav-item category-item";
            var categorya= document.createElement("a");
            categorya.classList.add("nav-link");
            categorya.classList.add("category-item");
            var cat = categoriesList[i].toLowerCase();
            categorya.textContent = cat.charAt(0).toUpperCase() + cat.slice(1);
            var category = categoriesList[i].replace(/\s/g, "_");
            var url_products  = baseURL+"/rest/product/category/" + category + "/" + id_sagra;
            categorya.addEventListener("click",function(event){
                    for(let j=0; j<categoriesdiv.childNodes.length; j++){

                        categoriesdiv.childNodes[j].className="nav-link category-item";
                    }
                    event.target.className="nav-link category-item-selected";

                }
            );
            categorya.addEventListener("click",generateProductHandler(url_products));
            //categoryli.appendChild(categorya);
            categoriesdiv.appendChild(categorya);
            if(i===0){
                firstCategory=categorya;
            }
        }

        if(firstCategory !== null && firstCategory !== undefined){
            firstCategory.click();
        }
    }else{
        let categoriesNav= document.getElementById("categories-nav");
        let categoryParent=categoriesNav.parentNode;
        categoryParent.removeChild(categoriesNav);
        printError("No products available!",20);
    }

}

function generateProductHandler(url){
    return function() {
        //clear_products_list();
        generate_products_list(url);

    };
}

function clear_products_list(){
    var productListElement = document.getElementById("products_list");
    while (productListElement.firstChild) {
        productListElement.removeChild(productListElement.firstChild);
    }
}
function generate_products_list(url_products){
    let url = url_products;
    let xhr = new XMLHttpRequest();
    xhr.open("GET", url);
    xhr.setRequestHeader('Accept', 'application/json');
    xhr.onload=function (){
        if(xhr.status===200){
            var responseObject = JSON.parse(xhr.responseText);
            const productObject = responseObject['resource-list'].map(item =>item.product);
            product_list = productObject.map(product=>[product.name,product.price,product.photo,product.description]);
            generate_product_list_by_categories(product_list);

        }else{
            printError("Unable to load products!",20);
        }
    }
    xhr.send();

}

function generate_product_list_by_categories(productsList){

    var productContainer = document.getElementById("products-container");
    productContainer.innerHTML = "";
    for (let i = 0; i < productsList.length; i++) {
        (function () {
            var product = productsList[i];
            var name = product[0];
            var price = product[1].toFixed(2);
            var photo = product[2];

            var row = document.createElement("div");
            row.classList.add("product-row");
            row.classList.add("row");
            row.classList.add("gx-0");
            row.classList.add("shadow-sm", "bg-white", "rounded", "align-items-center");
            row.setAttribute("data-bs-toggle", "modal");
            row.setAttribute("data-bs-target", "#staticBackdrop");
            row.addEventListener("click", function () {
                modifyPopup(product);
            });


            //Adding the image
            let col_img = document.createElement("div");
            col_img.classList.add("col");
            let imageElement = document.createElement("img");
            console.log(baseURL);
            imageElement.src = photo || baseURL + "/media/dish_default.png";
            imageElement.alt = "";
            imageElement.classList.add("product-image");
            col_img.appendChild(imageElement);
            row.appendChild(col_img);

            //Adding the name
            let col_name = document.createElement("div");
            col_name.classList.add("col", "text-center", "prod-name");
            col_name.innerHTML = name;

            row.appendChild(col_name);
            //Adding the price
            let col_price = document.createElement("div");
            col_price.classList.add("col","price-col", "text-center");
            col_price.innerHTML = Number(price).toFixed(2)+" \u20AC";

            row.appendChild(col_price);


            productContainer.appendChild(row);
        })();


    }


}
function modifyPopup(product) {
    var popupTitle = $("#staticBackdropLabel");
    popupTitle.html(product[0]);
    var dishDescription = $("#dishDescription");
    dishDescription.html("");
    dishDescription.html(product[3]);
    var minusQuantity = $("#minusbut");
    var plusQuantity = $("#plusbut");
    var closebutton = $("#closebut");
    var modalCloseButton = $('[data-bs-dismiss="modal"]');
    var addToCartButton = $("#addbut");

    function decreaseQuantityWrapper() {
        decreaseQuantity(addToCartButton);
    }

    function increaseQuantityWrapper() {
        increaseQuantity(addToCartButton);
    }

    minusQuantity.off();
    plusQuantity.off();

    addToCartButton.attr("data-name", product[0]);
    addToCartButton.attr("data-price", product[1]);
    addToCartButton.attr("data-quantity", 1);

    minusQuantity.on("click", decreaseQuantityWrapper);
    plusQuantity.on("click", increaseQuantityWrapper);
    closebutton.on("click", resetQuantity);
    modalCloseButton.on("click", resetQuantity);
}
function resetQuantity() {
    document.getElementById("quantity").textContent = 1;
}


function increaseQuantity(button) {
    var quantityElement = $("#quantity");
    var currentQuantity = quantityElement.text();
    var quantityValue = parseInt(currentQuantity);
    //console.log("increasing");
    quantityElement.text(quantityValue + 1);
    button.attr("data-quantity", quantityValue + 1);
}

function decreaseQuantity(button) {
    var quantityElement = $("#quantity");
    var currentQuantity = quantityElement.text();
    var quantityValue = parseInt(currentQuantity);
    if (quantityValue > 1) {
        //console.log("decreasing");
        quantityElement.text(quantityValue - 1);
        button.attr("data-quantity", quantityValue - 1);
    }
}
document.addEventListener("DOMContentLoaded", function() {
    shoppingCart = (function () {
        // =============================
        // Private methods and properties
        // =============================
        cart = [];

        // Constructor
        function Item(name, price, count) {
            this.name = name;
            this.price = price;
            this.count = count;
        }

        // Save cart
        function saveCart() {
            sessionStorage.setItem('shoppingCart', JSON.stringify(cart));
        }

        // Load cart
        function loadCart() {
            cart = JSON.parse(sessionStorage.getItem('shoppingCart'));
        }

        if (sessionStorage.getItem("shoppingCart") != null) {
            loadCart();
        }


        // =============================
        // Public methods and propeties
        // =============================
        var obj = {};

        // Add to cart
        obj.addItemToCart = function (name, price, count=1) {
            for (let item in cart) {
                if (cart[item].name === name) {
                    cart[item].count += count;
                    saveCart();
                    return;
                }
            }
            var item = new Item(name, price, count);
            cart.push(item);
            saveCart();
        }
        // Set count from item
        obj.setCountForItem = function (name, count) {
            for (var i in cart) {
                if (cart[i].name === name) {
                    cart[i].count = count;
                    break;
                }
            }
        };
        // Remove item from cart
        obj.removeItemFromCart = function (name) {
            for (var item in cart) {
                if (cart[item].name === name) {
                    cart[item].count--;
                    if (cart[item].count === 0) {
                        cart.splice(item, 1);
                    }
                    break;
                }
            }
            saveCart();
        }

        // Remove all items from cart
        obj.removeItemFromCartAll = function (name) {
            for (var item in cart) {
                if (cart[item].name === name) {
                    cart.splice(item, 1);
                    break;
                }
            }
            saveCart();
        }

        // Clear cart
        obj.clearCart = function () {
            cart = [];
            saveCart();
        }

        // Count cart
        obj.totalCount = function () {
            var totalCount = 0;
            for (var item in cart) {
                totalCount += cart[item].count;
            }
            return totalCount;
        }

        // Total cart
        obj.totalCart = function () {
            var totalCart = 0;
            for (var item in cart) {
                totalCart += cart[item].price * cart[item].count;
            }
            return Number(totalCart.toFixed(2));
        }

        // List cart
        obj.listCart = function () {
            var cartCopy = [];
            for (i in cart) {
                item = cart[i];
                itemCopy = {};
                for (p in item) {
                    itemCopy[p] = item[p];

                }
                itemCopy.total = Number(item.price * item.count).toFixed(2);
                cartCopy.push(itemCopy)
            }
            return cartCopy;
        }

        obj.getJsonCart = function (){
            return JSON.stringify(cart);
        }

        // cart : Array
        // Item : Object/Class
        // addItemToCart : Function
        // removeItemFromCart : Function
        // removeItemFromCartAll : Function
        // clearCart : Function
        // countCart : Function
        // totalCart : Function
        // listCart : Function
        // saveCart : Function
        // loadCart : Function
        return obj;
    })();

// *****************************************
// Triggers / Events
// *****************************************
// Add item
    $('.add-to-cart').click(function (event) {
        event.preventDefault();
        const addbut = event.currentTarget;

        // Extract the attributes
        var name = addbut.getAttribute('data-name');
        var price = Number(addbut.getAttribute('data-price'));
        var quantity = Number(addbut.getAttribute('data-quantity'));

        if (name && price && quantity) {
            shoppingCart.addItemToCart(name, price, quantity);
            displayCart();
        } else {
            console.log('Error: Missing attribute values');
        }
    });

// Clear items
    $('.clear-cart').click(function () {
        var confirmed = confirm("Are you sure you want to empty the cart?");

        if (confirmed) {
            shoppingCart.clearCart();
            displayCart();
        }
    });

    $('#orderbut').click(function () {
        if (shoppingCart.totalCount() === 0) {
            alert('Your shopping cart is empty. Please add items before placing an order.');
            return;
        }

        var name = $('#nameInput').val();
        name = sanitizeInput(name);
        if (name.trim() === '') {
            alert('Please enter your name.');
            return;
        }
        if (name.length >50){
            alert('Name too long');
            return;
        }

        var table = $('#tableInput').val();
        table = sanitizeInput(table);
        if (table.length >10){
            alert('Table format not valid');
            return;
        }

        var seats = Number($('#seatsInput').val());
        seats = sanitizeInput(seats);
        if (isNaN(seats) || seats < 0) {
            alert('Please enter a positive integer for the number of seats.');
            return;
        }

        var email = $('#emailInput').val();
        email = sanitizeInput(email);
        if (email.trim() !== '' && !validateEmail(email) && email.length>50) {
            alert('Please enter a valid email address.');
            return;
        }

        var sent = sendOrder(shoppingCart.getJsonCart(), name, table, seats, email, shoppingCart.totalCart());
        if(sent){
            shoppingCart.clearCart();
            displayCart();
        }
        $('#cart').modal('hide');
    });


// Delete item button

    $('.show-cart').on("click", ".delete-item", function (event) {
        var name = event.target.getAttribute('data-name');
        var confirmed = confirm("Are you sure you want to delete this item from the cart?");

        if (confirmed) {
            shoppingCart.removeItemFromCartAll(name);
            displayCart();
        }
    })


// -1
    $('.show-cart').on("click", ".minus-item", function (event) {
        var name = event.target.getAttribute('data-name');
        shoppingCart.removeItemFromCart(name);
        displayCart();
    })
// +1
    $('.show-cart').on("click", ".plus-item", function (event) {
        var name = event.target.getAttribute('data-name');
        shoppingCart.addItemToCart(name);
        displayCart();
    })

// Item count input
    $('.show-cart').on("change", ".item-count", function (event) {
        var name = $(this).data('name');
        var count = Number($(this).val());
        shoppingCart.setCountForItem(name, count);
        displayCart();
    });


    clearAll();

    //adding product in case an order to modify is passed
    let queryString=window.location.search;
    let params= new URLSearchParams(queryString);
    var idOrder = params.get('order');
    var idSagra = params.get('sagra');

    if(idOrder != null && idSagra!=null){
        //printError("log cart",10);
        let url = baseURL+"/rest/order/"+idOrder+"/" + idSagra; //CHANGED FROM HTTPS!!
        var xhr= new XMLHttpRequest();
        xhr.open("GET", url);
        //xhr.setRequestHeader('Accept', 'application/json');
        xhr.onload=function () {
            if(xhr.status === 200) {
                var orderObject = JSON.parse(xhr.responseText)['order'];
                $('#nameInput').val(orderObject['client_name']);
                $('#tableInput').val(orderObject['table_number']);
                $('#seatsInput').val(orderObject['client_num']);
                $('#emailInput').val(orderObject['email']);
                const contentList = orderObject['content'].map(item => item.orderContent);
                console.log(contentList);
                fillCart(contentList);
                displayCart();
            }else{
                printError("Unable to load the order for modifications",5);
            }
        };
        xhr.send();
    }

    displayCart();

});

//function to display cart
function displayCart() {
    var cartArray = shoppingCart.listCart();
    var output = "";
    for (var i in cartArray) {
        output += "<div class='row g-0 shadow-sm bg-light rounded cart-row'>"
            + "<div class='col-4'>"
            + "<div class='card-body'>"
            + "<p class='card-header'>" + cartArray[i].name + "</p>"
            + "<p class='card-text'>(" + Number(cartArray[i].price).toFixed(2)+ " \u20AC" + ")</p>"
            + "</div>"
            +" </div>"
            + "<div class='col-8'>"
            + "<div class='row g-0'>"
            +" <div class='col-6'>"
            + "<div class='input-group input-group-sm mb-3'><button class='minus-item input-group-addon btn btn-primary' data-name='" + cartArray[i].name + "'>-</button>"
            + "<input type='number' class='item-count form-control' data-name='" + cartArray[i].name + "' value='" + cartArray[i].count + "'>"
            + "<button class='plus-item btn btn-primary input-group-addon' data-name='" + cartArray[i].name + "'>+</button>"
            + "</div>"
            + "</div>"
            + "<div class='col-4  align-self-center'>"
            + "<p class='partial-price'>" + Number(cartArray[i].total).toFixed(2) + " \u20AC" + "</p>"
            +"</div>"
            +" <div class='col-2 align-self-center'>"
            + "<button class='delete-item btn btn-danger' data-name='" + cartArray[i].name + "'>X</button>"
            +"</div>"
            + "</div>"
            +" </div>"



            + "</div>";
    }
    $('.show-cart').html(output);
    $('.total-cart').html(Number(shoppingCart.totalCart()).toFixed(2));
    $('#total-counter').html(shoppingCart.totalCount());
}

function sanitizeInput(input) {
    var sanitizedInput = $('<div>').text(input).html();
    return sanitizedInput;
}
function validateEmail(email) {
    // Regular expression for email validation
    var emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailPattern.test(email);
}

function sendOrder(cart, customerName, tableNumber, seatsNumber, email, totalPrice){



    let queryString=window.location.search;
    let params= new URLSearchParams(queryString);
    var idSagra = params.get('sagra');
    var idOrder = params.get('order');

    var jsonArray = JSON.parse(cart);
    var content;
    if(idOrder != null){
        content = jsonArray.map(item => {
            var { name, price, count } = item;
            return {
                "orderContent": {
                    "id_sagra": Number(idSagra),
                    "id_order": Number(idOrder),
                    "product_name": name,
                    "price": price,
                    "quantity": count
                }
            };
        });
    }else{
        content = jsonArray.map(item => {
            var { name, price, count } = item;
            return {
                "orderContent": {
                    "id_sagra": Number(idSagra),
                    "id_order": -1,
                    "product_name": name,
                    "price": price,
                    "quantity": count
                }
            };
        });
    }


    var order = {
        "id":-1,
        "client_name": customerName,
        "email": email,
        "client_num": Number(seatsNumber),
        "table_number": tableNumber,
        "content": content
    };

    if(idOrder != null){
        order['id']=Number(idOrder);
    }

    if (email.length === 0){
        delete order.email;
    }

    if (tableNumber.length === 0){
        delete order.table_number;
    }

    var fullOrder = JSON.stringify({ order: order }, null, 2);

   // Log the JSON string
   // console.log(fullOrder);

    if(idOrder == null){ //if the order is a new order we use rest to submit the order
        let url = baseURL+"/rest/order"; //CHANGED FROM HTTPS!!
        var xhr= new XMLHttpRequest();
        xhr.open("POST", url);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.onload=function () {
            if(xhr.status === 201) {
                var responseObject = JSON.parse(xhr.responseText);
                var idOrder = responseObject["order"].id;
                let urlPdf = baseURL+"/DownloadSummaryPDF?order=" + idOrder + "&sagra=" + idSagra;

                $('#full-container').css('display' ,'none')
                $('#order_id').text(idOrder)
                $('#totalValue').text(Number(totalPrice).toFixed(2))
                $('#global_wrapper').css('display', 'block')

                $("#download_pdf").click(function() {
                    // Redirect to the desired URL
                    window.location.href = urlPdf;
                });

                clearAll(); //clearing cart
                return true;
            }else{
                printError("Unable to send order",10);
            }
        };
        xhr.send(fullOrder);
        return false;
    }

    //if the order is an order to be modified we send the request to the correct servlet

    //creating a fictitius form

    const totalform =document.createElement("form");
    totalform.action=baseURL+"/neworder";
    totalform.method="POST";
    totalform.id="order-form";
    document.getElementsByTagName("body")[0].append(totalform);

    $('#order-form').css("display","none");

    let id_order=document.createElement("input");
    id_order.type="text";
    id_order.value=idOrder;
    id_order.name="id_order";

    const form=document.getElementById("order-form");

    form.appendChild(id_order);

    let client_name=document.createElement("input");
    client_name.type="text";
    client_name.value=customerName;
    client_name.name="client_name";
    form.appendChild(client_name);

    let client_num=document.createElement("input");
    client_num.type="text";
    client_num.value=seatsNumber;
    client_num.name="client_num";
    form.appendChild(client_num);

    if (email.length !== 0){
        let emailf=document.createElement("input");
        emailf.type="text";
        emailf.value=email;
        emailf.name="email";
        form.appendChild(emailf);
    }

    if (tableNumber.length !== 0){
        let table_number=document.createElement("input");
        table_number.text="text";
        table_number.value=tableNumber;
        table_number.name="table_number";
        form.appendChild(table_number);
    }

    //getting array of element
    let cartArray=JSON.parse(cart);

    //adding fields for each product
    for(let k=0; k<cartArray.length; k++){
        let product=document.createElement("input");
        product.text="text";
        product.value=cartArray[k].name;
        product.name="products";
        form.appendChild(product);

        let quantity=document.createElement("input");
        quantity.text="text";
        quantity.value=cartArray[k].count;
        quantity.name="quantity";
        form.appendChild(quantity);
    }

    //forwarding the request to the proper page

    clearAll();

    //sending request
    form.submit();




    return false;

}


//function to fill cart in case of modified order
function fillCart(contentList){
    for(let j=0; j<contentList.length; j++){
        shoppingCart.addItemToCart(contentList[j]['product_name'],contentList[j]['price'],Number(contentList[j]['quantity']));
    }
}

//function to clear cart and close popup
function clearAll(){
    shoppingCart.clearCart();
    $('#nameInput').val("");
    $('#tableInput').val("");
    $('#emailInput').val("");
    $('#seatsInput').val("");
    displayCart();
    $('#close-cart').click();
}
//function to print error on top of page
function printError(error,time){
    $('#error-alert').text(error).css("display","block");
    window.setTimeout(function (){
        $('#error-alert').text("").css("display","none");
    },time*1000);
}
