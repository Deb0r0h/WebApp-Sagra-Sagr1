/*
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

 Author: Diego Spinosa (diego.spinosa@studenti.unipd.it)
 Version: 1.0
 Since: 1.0
*/

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
