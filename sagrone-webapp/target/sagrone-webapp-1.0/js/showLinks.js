function searchAndModifyElement() {
  const url = window.location.href;
  console.log("url:");
  console.log(url);
  var element;
  if ( url.indexOf("/orders") !== -1) {

    element = document.getElementById("ordersLink");

  }else if( url.indexOf("/payedorders") !== -1) {
    element = document.getElementById("paidOrdersLink");

  }else if( url.indexOf("/menu") !== -1) {
    element = document.getElementById("menuLink");

  }else if( url.indexOf("/seeprod") !== -1) {
    element = document.getElementById("productsLink");

  }else if( url.indexOf("/users") !== -1) {
    element = document.getElementById("usersLink");
  }

  if (element) {
    element.classList.add("text-highlighted");
  }
}

document.addEventListener('DOMContentLoaded', function() {
  searchAndModifyElement();
});