
/**
    * @param {string} title
    * @param {string} desc
    * @param {function} callback
    * @param {string} type - "success" or "error"
*/
function openPopupOnConfirmation(title, desc, callback, type, btn_text) {
  $(document).ready(function() {
      const popup = new Popup(title, desc, callback, type, btn_text);
      popup.open();
  });
}

function openPopupOnOrderContent(order_content) {
  $(document).ready(function() {
      const popup = new Popup("Order Content", order_content, "ordercontent", undefined);
      popup.open();
  });
}


 function updateFormAction() {
   const url = window.location.href;
   const form = document.getElementById('myForm');
   const sortform = document.getElementById('sortForm');

   var actionUrl = "";
   if ( url.indexOf("/orders") !== -1) {
       actionUrl = 'orders';
   }else if( url.indexOf("/payedorders") !== -1) {
       actionUrl = 'payedorders';
   }

    form.setAttribute('action', actionUrl);
    sortform.setAttribute('action', actionUrl);
}

function sendPDFRequest(URL, orderID, sagraID) {
  const xhr = new XMLHttpRequest();
  const url = URL.trim();
  const params = `order=${orderID.trim()}&sagra=${sagraID.trim()}`;

  xhr.open('GET', `${url}?${params}`, true);
  xhr.responseType = 'blob';

  // download PDF and open a new tab
  xhr.onload = function () {
    if (xhr.status === 200) {
      const headers = xhr.getAllResponseHeaders();
      const fileName = getFileNameFromHeaders(headers);
      const blob = new Blob([xhr.response], { type: 'application/pdf' });
      saveAs(blob, fileName);
    } else {
      console.error('Error downloading PDF:', xhr.status, xhr.statusText);
      alert('Error downloading PDF. Please try again later.');
    }
  };

  xhr.onerror = function () {
    console.error('Error requesting PDF');
    alert('Error requesting PDF. Please try again later.');
  };

  xhr.send();
}

// Helper function to extract filename from headers
function getFileNameFromHeaders(headers) {
  const contentDisposition = headers.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/i);
  if (contentDisposition && contentDisposition.length >= 2) {
    return contentDisposition[1].replace(/['"]/g, '');
  }
  return 'order.pdf'; // Fallback filename
}

// Funzione per aprire il popup e aggiungere i dati
function openOrderDetailsModal(clientName, email, clientNum, tableNumber, idUser, orderTime, paymentTime) {
  // Apri il popup
  const orderDetailsModal = new bootstrap.Modal(document.getElementById("orderDetailsModal"));
  orderDetailsModal.show();

  // Aggiungi i dati del cliente ai rispettivi elementi HTML
  document.getElementById("clientName").textContent = clientName;
  document.getElementById("email").textContent = email;
  document.getElementById("numberOfClients").textContent = clientNum;
  document.getElementById("numberOfTable").textContent = tableNumber;
  document.getElementById("cashier").textContent = idUser > 0? idUser : "Unpaid";
  document.getElementById("orderTime").textContent = orderTime? orderTime : "-";
  document.getElementById("paymentTime").textContent = paymentTime? paymentTime : "-";
}

// Function to populate the order content table
function populateOrderContentTable(orderContentData, payed) {

  const tableBody = document.getElementById('orderContentTableBody');

  // Remove any previous rows
  while (tableBody.firstChild) {
    tableBody.firstChild.remove();
  }
  let total = 0;
  // Populate the table with data
  orderContentData.forEach(data => {
    const row = document.createElement('tr');
    const productNameCell = document.createElement('td');
    const quantityCell = document.createElement('td');
    const xCell = document.createElement('td');
    const priceCell = document.createElement('td');

    productNameCell.textContent = data.orderContent.product_name;
    quantityCell.textContent = data.orderContent.quantity;
    xCell.textContent = 'x';
    priceCell.textContent = data.orderContent.price.toFixed(2) + ' €';

    quantityCell.style.textAlign = 'center';
    priceCell.style.textAlign = 'center';
    xCell.style.color = 'lightgray';

    row.appendChild(productNameCell);
    row.appendChild(quantityCell);
    row.appendChild(xCell);
    row.appendChild(priceCell);

    tableBody.appendChild(row);

    const subtotal = data.orderContent.quantity * data.orderContent.price;
    total += subtotal;
  });
  // Display the total value
  totalValue.textContent = total.toFixed(2) + ' €';

  var orderID = orderContentData[0].orderContent.id_order;
  var sagraID = orderContentData[0].orderContent.id_sagra;


  const orderIDField = document.getElementById('orderIDField');
  const modOrderField = document.getElementById('modOrderField');
  const modSagraField = document.getElementById('modSagraField');
  const deleteOrderField = document.getElementById('deleteOrderField');

  orderIDField.value = orderID;
  modOrderField.value = orderID;
  modSagraField.value = sagraID;
  deleteOrderField.value = orderID;

  const form = document.querySelector('.payForm');
  const payUnpayBtn = document.getElementById('payUnpayBtn');

  var additionalPath = "";
  console.log("payed alla fine :");
  console.log(payed);
  console.log(typeof payed);
    if(payed === "true") {
      //additionalPath = "payedorders/unpay";
      //payUnpayBtn.textContent = 'UNPAY';
      form.style.display = 'none';
    }else {
      additionalPath = "orders/pay";
      payUnpayBtn.textContent = 'PAY';
    }

  const baseAction = form.getAttribute('data-action');

  const newAction = baseAction + additionalPath;

  form.setAttribute('data-action', newAction);
  form.setAttribute('action', newAction);


}

function getTotal(element) {

    var id = element.getAttribute('data-id');

    var sagra = element.getAttribute('sagra');

    var base_url = element.getAttribute('base_URL');

    var url = base_url + '/rest/order/' + id + '/' + sagra;
    fetch(url)
      .then(response => {
        if (response.ok) {
          return response.json();
        } else if (response.status === 400) {
          throw new Error('Wrong URI');
        } else if (response.status === 500) {
          throw new Error('Server Error');
        } else {
          throw new Error('Unknown Error');
        }
      })
      .then(data => {
        var total = 0;

        data.order.content.forEach(data => {

            const subtotal = data.orderContent.quantity * data.orderContent.price;
            total += subtotal;
        });

        if(total > 0) {
            element.textContent = total.toFixed(2) + ' €';
        }
      })
      .catch(error => {
        console.error('Error:', error);
        // Handle the error and show an error message
      });
}

function openPopupOnOrderContent(base_url,order_id, sagra_id, payed) {
    // Make the GET request
    var url = base_url + '/rest/order/' + order_id + '/' + sagra_id;
    fetch(url)
      .then(response => {
        if (response.ok) {
          return response.json();
        } else if (response.status === 400) {
          throw new Error('Wrong URI');
        } else if (response.status === 500) {
          throw new Error('Server Error');
        } else {
          throw new Error('Unknown Error');
        }
      })
      .then(data => {

        populateOrderContentTable(data.order.content, payed);

        // Show the modal
        const modal = new bootstrap.Modal(document.getElementById('orderContentModal'));
        modal.show();
      })
      .catch(error => {
        console.error('Error:', error);
        // Handle the error and show an error message
      });
}

function getTimeDiffText(timestamp) {
  const currentTime = new Date();
  const diffTime = currentTime - timestamp;

  const minutes = Math.floor(diffTime / (1000 * 60));
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);

  if (minutes < 1) {
    return "just now";
  } else if (minutes < 60) {
    return `${minutes} min ago`;
  } else if (hours < 24) {
    return `${hours} hours ago`;
  } else if (days === 1) {
    return "yesterday";
  } else {
    return `${days} days ago`;
  }
}


function updateTimestamps() {
  const elements = document.querySelectorAll('td.timestamp');

  elements.forEach((element) => {
    var content = element.textContent.trim();

    if( content === "-") {
        return;
    }

    const timestamp = new Date(content);

    const newText = getTimeDiffText(timestamp);

    element.textContent = newText;
  });
}


function hideConfirmationContainer() {
    var confirmationContainer = document.getElementById("confirmationContainer");
    //if exists
    if(confirmationContainer) {
        confirmationContainer.style.display = "none";
    }
}

document.addEventListener('DOMContentLoaded', function() {

  var Content_buttons = document.getElementsByClassName('contentButton');


  //TOTAL
  const totalTDs = document.querySelectorAll('#totalTD');
    totalTDs.forEach(function(element) {
      getTotal(element);
    });

  // Loop through the buttons
  for (var i = 0; i < Content_buttons.length; i++) {
    var button = Content_buttons[i];

    // Add a click event listener to each button
    button.addEventListener('click', function() {
      var id = this.getAttribute('data-id');
      var sagra = this.getAttribute('sagra');

      var base_url = this.getAttribute('base_URL');

      var payed = this.getAttribute('payed');
      console.log("payed");
      console.log(payed);

      openPopupOnOrderContent(base_url,id, sagra, payed);

    });
  }

  var info_buttons = document.getElementsByClassName('infoButton');
  // Loop through the buttons
    for (var i = 0; i < info_buttons.length; i++) {
      var button = info_buttons[i];

      // Add a click event listener to each button
      button.addEventListener('click', function() {
        var clientName = this.getAttribute('clientName');
        var email = this.getAttribute('email');
        var numberOfClients = this.getAttribute('numberOfClients');
        var numberOfTable = this.getAttribute('numberOfTable');
        var cashier = this.getAttribute('cashier');
        var orderTime = this.getAttribute('orderTime');
        var paymentTime = this.getAttribute('paymentTime');

        openOrderDetailsModal(clientName, email, numberOfClients, numberOfTable, cashier, orderTime, paymentTime);

      });
    }

    updateTimestamps();

    setTimeout(hideConfirmationContainer, 7000);


     const url = window.location.href;
     const EditBtn = document.querySelector('.editBtn');

     var actionUrl = "";
      if( url.indexOf("/payedorders") !== -1) {

         for (var i = 0; i < EditBtn.length; i++) {
             EditBtn[i].style.display = "none";
         }
     }
});

$(document).ready(function() {
  $('.info-icon').click(function() {
    $(this).siblings('.popup-container').toggleClass('show');
  });
});





