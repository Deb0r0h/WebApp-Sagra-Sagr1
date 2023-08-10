function handleFileSelect(evt) {
    evt.stopPropagation();
    evt.preventDefault();

    var files = evt.dataTransfer ? evt.dataTransfer.files : evt.target.files; // Supporto sia per il drag and drop che per la scelta del file tramite bottone
    var output = document.getElementById("preview");

    // Rimuovi immagine precedente
    output.innerHTML = "";

    for (var i = 0, f; (f = files[i]); i++) {
        if (!f.type.match("image.*")) {
            continue;
        }

        var reader = new FileReader();

        reader.onload = (function (theFile) {
            return function (e) {
                var span = document.createElement("span");
                span.innerHTML = '<img class="thumb" src="' + e.target.result + '" title="' + escape(theFile.name) + '" />';
                output.insertBefore(span, null);
                output.classList.add("withTitle"); // Aggiungi la classe "withTitle"
                document.getElementById("removeButton").style.display = "inline"; // Mostra il bottone di rimozione
            };
        })(f);

        reader.readAsDataURL(f);
    }
}

function handleDragOver(evt) {
    evt.stopPropagation();
    evt.preventDefault();
    evt.dataTransfer.dropEffect = "copy";
}

function handleRemoveImage() {
    var output = document.getElementById("preview");
    output.innerHTML = "";
    document.getElementById("removeButton").style.display = "none"; // Nascondi il bottone di rimozione
}

function init() {
    var dropArea = document.getElementById("dropArea");

    dropArea.addEventListener("dragover", handleDragOver, false);
    dropArea.addEventListener("drop", handleFileSelect, false);

    var fileInput = document.getElementById("fileInput");
    fileInput.addEventListener("change", handleFileSelect, false);

    var removeButton = document.getElementById("removeButton");
    removeButton.addEventListener("click", handleRemoveImage, false);
}
