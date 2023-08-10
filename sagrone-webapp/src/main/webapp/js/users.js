//ADD <-> CLOSE BUTTON CHANGE ON CLICK:
let ab = $(".card-header button");
ab.click( function(){
    if(ab.text() == "Add") ab.text("Close");
    else ab.text("Add");
    ab.toggleClass("btn btn-secondary");
    ab.toggleClass("btn btn-outline-secondary");
});


//EDIT <-> CLOSE BUTTON CHANGE ON CLICK:
let eb = $(".list-group-item button");
eb.click( function(){
    let e = $(this);
    if(e.text() == "Edit") e.text("Close");
    else e.text("Edit");
    e.toggleClass("btn btn-primary");
    e.toggleClass("btn btn-outline-primary");
});

//FORM VALIDATION: Code to allow Bootstrap styling
const forms = document.querySelectorAll('.needs-validation')

Array.from(forms).forEach(form => {
    form.addEventListener('submit', event => {
      if(event.submitter.className == 'btn btn-primary'){   //'delete' buttons are 'submit' (because POST) but don't need input to be valid.
        if (!form.checkValidity()) {
            event.preventDefault()
            event.stopPropagation()
        }
        form.classList.add('was-validated')
      }
    }, false)
})
