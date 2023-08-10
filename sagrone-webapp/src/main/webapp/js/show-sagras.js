var search_input = document.getElementById("search_input");
var reset_btn = document.getElementById("reset_btn");
var card_titles = document.querySelectorAll(".card-title");

search_input.addEventListener("input", function() {
    let search_text = this.value.trim().toLowerCase();
    for(let i=0; i<card_titles.length; i++){
        card_title_text = card_titles[i].textContent;
        if(card_title_text.toLowerCase().includes(search_text))
            card_titles[i].closest(".card").parentElement.style.display = "block";
        else
            card_titles[i].closest(".card").parentElement.style.display = "none";

    }
});

reset_btn.addEventListener("click", function() {
    for(let i=0; i<card_titles.length; i++){
        card_titles[i].closest(".card").parentElement.style.display = "block";
    }
    search_input.value="";
});

var btn_scroll_top = document.getElementById("btn_scroll-top");
window.onscroll = function () {
    if (window.pageYOffset > 20)
        btn_scroll_top.style.display = "block";
    else
        btn_scroll_top.style.display = "none";
};

btn_scroll_top.addEventListener("click", function() {
     window.scrollTo({
       top: 0,
       behavior: "smooth",
     });
 });



