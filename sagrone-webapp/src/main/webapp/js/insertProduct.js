$(document).ready(function () {
            function readURL(input) {
                if (input.files && input.files[0]) {
                    var reader = new FileReader();
                    reader.onload = function (e) {
                        $('#preview-insert-image').attr('src', e.target.result).show();
                        $('#remove-insert-image').show();
                    }
                    reader.readAsDataURL(input.files[0]);
                }
            }

            function resetPreview(event) {
                event.preventDefault()
                $('#preview-insert-image').attr('src', '').hide();
                $('#remove-insert-image').hide();
                $('#photo-insert').val('');
            }

            $('#photo-insert').on('change', function () {
                readURL(this);
            });

            $('#photo-insert-dropzone').on('dragover', function (e) {
                e.preventDefault();
                e.stopPropagation();
                $(this).css('background-color', '#e0e0e0');
            }).on('dragleave', function (e) {
                e.preventDefault();
                e.stopPropagation();
                $(this).css('background-color', '');
            }).on('drop', function (e) {
                e.preventDefault();
                e.stopPropagation();
                $(this).css('background-color', '');
                var files = e.originalEvent.dataTransfer.files;
                $('#photo-insert').prop('files', files);
                readURL($('#photo-insert')[0]);
            });

            $('#remove-insert-image').on('click', function (event) {
                resetPreview(event);
            });
 });



document.addEventListener('DOMContentLoaded', function() {
    $.ajax({
        url: 'http://localhost:8080/sagrone-webapp-1.0/rest/categories/',
        type: 'GET',
        dataType: 'json',
        success: function(response) {

            var categories = response['resource-list'].map(function(item) {
                return item.category.name;
            });
            var selectElement = document.getElementById('categoryInsert');
            var existingOptions = Array.from(selectElement.options).map(function(option) {
                return option.value;
            });
            categories.forEach(function(category) {
                if (!existingOptions.includes(category)) {
                    var option = document.createElement('option');
                    option.text = category;
                    option.value = category;
                    selectElement.appendChild(option);
                }
            });
            console.log(`Food categories correctly retrieved:`);
            categories.forEach((category) => {console.log(`\t${category}\n`)});
        },
        error: function (response){
            let errorDetails = JSON.parse(response.responseText).message;
            console.error(`Bad response while retrieving food categories: \n\tMessage: ${errorDetails['message']}\n\tError code: ${errorDetails['error-code']}\n\tError details: ${errorDetails['error-details']}`)
        }
    });
});

document.addEventListener('DOMContentLoaded', function() {
    $.ajax({
        url: 'http://localhost:8080/sagrone-webapp-1.0/rest/categories/',
        type: 'GET',
        dataType: 'json',
        success: function(response) {

            var categories = response['resource-list'].map(function(item) {
                return item.category.name;
            });
            var selectElement = document.getElementById('categoryUpdate');
            var existingOptions = Array.from(selectElement.options).map(function(option) {
                return option.value;
            });
            categories.forEach(function(category) {
                if (!existingOptions.includes(category)) {
                    var option = document.createElement('option');
                    option.text = category;
                    option.value = category;
                    selectElement.appendChild(option);
                }
            });
            console.log(`Food categories correctly retrieved:`);
            categories.forEach((category) => {console.log(`\t${category}\n`)});
        },
        error: function (response){
            let errorDetails = JSON.parse(response.responseText).message;
            console.error(`Bad response while retrieving food categories: \n\tMessage: ${errorDetails['message']}\n\tError code: ${errorDetails['error-code']}\n\tError details: ${errorDetails['error-details']}`)
        }
    });

});


//change ',' with '.' on price in the form
window.addEventListener('DOMContentLoaded', function() {
    var submitButton = document.getElementById('submitInsert');
    submitButton.addEventListener('click', function() {
        var priceInput = document.getElementById('priceInsert');
        var priceValueInput = priceInput.value;
        // priceValueInput = priceValueInput.replace(',', '.');
        // priceInput.value = priceValueInput;

        var tempInsert = parseFloat(priceValueInput);
        priceInput.value = tempInsert.toFixed(2);

        priceValueInput = priceValueInput.replace(',', '.');
        priceInput.value = priceValueInput;

    });
});


window.addEventListener('DOMContentLoaded', function() {
    var submitButtonUpdate = document.getElementById('UpdateSub');
    submitButtonUpdate.addEventListener('click', function() {
        var priceInputUpdate = document.getElementById('price');
        var priceValueUpdate = priceInputUpdate.value;
        // priceValueUpdate = priceValueUpdate.replace(',', '.');
        // priceInputUpdate.value = priceValueUpdate;

        var tempUpdate = parseFloat(priceValueUpdate);
        priceInputUpdate.value = tempUpdate.toFixed(2);

        priceValueUpdate = priceValueUpdate.replace(',', '.');
        priceInputUpdate.value = priceValueUpdate;

    });
});


function validateName(input) {
    var illegalCharacters = /['"]/g;
    input.value = input.value.replace(illegalCharacters, '');
}








