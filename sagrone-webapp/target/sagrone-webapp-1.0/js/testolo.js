//to manage drag and drop box
var dropzone = document.getElementById('photo-dropzone');
var previewImage = document.getElementById('preview-image');
var fileInput = document.getElementById('photo');
var removeImageButton = document.getElementById('remove-image');

dropzone.addEventListener('dragover', function(e) {
    e.preventDefault();
    dropzone.style.backgroundColor = '#f7f7f7';
});

dropzone.addEventListener('dragleave', function(e) {
    e.preventDefault();
    dropzone.style.backgroundColor = 'transparent';
});

dropzone.addEventListener('drop', function(e) {
    e.preventDefault();
    dropzone.style.backgroundColor = 'transparent';

    var file = e.dataTransfer.files[0];
    displayImagePreview(file);
    fileInput.files = e.dataTransfer.files;
});

fileInput.addEventListener('change', function(e) {
    var file = e.target.files[0];
    displayImagePreview(file);
});

dropzone.addEventListener('click', function(e) {
    var fileInput = document.getElementById('photo');
    fileInput.click();
});

removeImageButton.addEventListener('click', function(e) {
    fileInput.value = '';
    previewImage.src = '#';
    previewImage.style.display = 'none';
    removeImageButton.style.display = 'none';
});

function displayImagePreview(file) {
    if (file && file.type.startsWith('image/')) {
        var reader = new FileReader();

        reader.onload = function(e) {
            previewImage.src = e.target.result;
            previewImage.style.display = 'block';
            removeImageButton.style.display = 'block';
        };

        reader.readAsDataURL(file);
    } else {
        previewImage.src = '#';
        previewImage.style.display = 'none';
        removeImageButton.style.display = 'none';
    }
}