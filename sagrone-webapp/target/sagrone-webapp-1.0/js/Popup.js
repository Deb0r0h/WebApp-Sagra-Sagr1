class Popup {
    constructor(title, desc, callback, type, btn_text) {
        this.title = title;
        this.desc = desc;
        this.callback = callback;
        this.type = type;
        this.btn_text = btn_text;
    }
    open() {
        let popupDiv = document.createElement("div");
        popupDiv.setAttribute("id", "popupDiv");
        popupDiv.classList.add("modal", "fade");
        popupDiv.tabIndex = -1;
        popupDiv.role = "dialog";

        let dialogDiv = document.createElement("div");
        dialogDiv.classList.add("modal-dialog", "rounded");
        dialogDiv.role = "document";

        let contentDiv = document.createElement("div");
        contentDiv.classList.add("modal-content");

        //TOP COLOR DIV
        let topDiv = document.createElement("div");
        if( this.type === "success" || this.type === "error" || this.type === "confirm"){
            topDiv.classList.add("rounded-top");

            var container = document.createElement("div");
            container.classList.add('container');
            var row = document.createElement("div");
            row.classList.add('row');
            row.classList.add('justify-content-center');
            var col = document.createElement("div");
            col.classList.add('col-auto');
            var circle = document.createElement("div");
            circle.classList.add('popup-circle');
            var span = document.createElement("span");

            if (this.type === "success") {
                topDiv.style.backgroundColor = "var(--success-color)";
                span.classList.add("bi", "bi-check");
            } else if (this.type === "error") {
                topDiv.style.backgroundColor = "var(--error-color)";
                span.classList.add("bi", "bi-x");
            }

            circle.append(span);
            col.append(circle);
            row.append(col);
            container.append(row);
            topDiv.appendChild(container);
        }

        let headerDiv = document.createElement("div");
        headerDiv.classList.add("modal-header");

        let title = document.createElement("h5");
        title.classList.add("modal-title");
        title.innerText = this.title;

        let closeButton = document.createElement("button");
        closeButton.type = "button";
        closeButton.classList.add("btn-close");
        closeButton.setAttribute("data-bs-dismiss", "modal");
        closeButton.setAttribute("aria-label", "Close");

        let bodyDiv = document.createElement("div");
        bodyDiv.classList.add("modal-body");

        //BODY CONTENT
        let desc = document.createElement("div");
        if( this.type !== "ordercontent"){
        let descText = document.createElement("p");
        descText.innerText = this.desc;
        desc.append(descText);
        }else{
          let table = document.createElement("table");
          table.classList.add("table", "table-responsive");
          table.style.width = "100%";

          let thead = document.createElement("thead");
          thead.classList.add("bg-light");
          let theadRow = document.createElement("tr");

          let th1 = document.createElement("th");
          th1.innerText = "Order Name";
          let th2 = document.createElement("th");
          th2.innerText = "Quantity";
          let th3 = document.createElement("th");
          th3.innerText = "Price";

          theadRow.appendChild(th1);
          theadRow.appendChild(th2);
          theadRow.appendChild(th3);
          thead.appendChild(theadRow);

          let tbody = document.createElement("tbody");

          desc.forEach(function(content) {
            let tr = document.createElement("tr");

            let td1 = document.createElement("td");
            td1.innerText = content.productName;

            let td2 = document.createElement("td");
            td2.innerText = content.quantity;

            let td3 = document.createElement("td");
            td3.innerText = content.price ;

            tr.appendChild(td1);
            tr.appendChild(td2);
            tr.appendChild(td3);

            tbody.appendChild(tr);
          });


          table.appendChild(thead);
          table.appendChild(tbody);

          desc.appendChild(table);
        }

        let footerDiv = document.createElement("div");
        footerDiv.classList.add("modal-footer");
        footerDiv.style.borderTop = "none";


        let okButton = document.createElement("button");
        okButton.type = "button";
        okButton.classList.add("btn", "btn-secondary");
        //if different from indefined set the text
        okButton.innerText = this.btn_text !== undefined ? this.btn_text : "OK"
        okButton.onclick = () => {
            this.callback();
            $(popupDiv).modal("hide");
        };
        //HEADER
        headerDiv.appendChild(title);
        headerDiv.appendChild(closeButton);
        //BODY
        bodyDiv.appendChild(desc);
        //FOOTER
        footerDiv.appendChild(okButton);

        contentDiv.appendChild(topDiv);
        contentDiv.appendChild(headerDiv);
        contentDiv.appendChild(bodyDiv);
        contentDiv.appendChild(footerDiv);
        dialogDiv.appendChild(contentDiv);
        popupDiv.appendChild(dialogDiv);
        document.body.appendChild(popupDiv);

        $(popupDiv).modal("show");

    }
}
