window.onload = function () {
    let ul = document.querySelector('ul');
    ul.className = 'list-group';

    let errors_list = document.querySelectorAll('ul li');
    error_list.forEach((err) => {
        err.className = 'list-group-item list-group-item-danger';
    });
};
