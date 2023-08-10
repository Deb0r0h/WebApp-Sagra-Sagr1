//this function removes the shown message after three seconds is displayed
window.onload = (event) => {
    var error=document.getElementById("message");
    if(error!=null){
        console.log("message event started!");
        setTimeout(() => {
                error.parentNode.removeChild(error);
                console.log("message event ended!");
        }, 3000);
    }
  };