window.onload = (event) => {
    console.log("submit event called!");
    var error=document.getElementById("error");
    setTimeout(() => {
        error.parentNode.removeChild(error);
    }, 3000);
  };