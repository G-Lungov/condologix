window.addEventListener('DOMContentLoaded', event => {

    // Navbar shrink function
    var navbarShrink = function () {
        const navbarCollapsible = document.body.querySelector('#mainNav');
        if (!navbarCollapsible) {
            return;
        }
        if (window.scrollY === 0) {
            navbarCollapsible.classList.remove('navbar-shrink')
        } else {
            navbarCollapsible.classList.add('navbar-shrink')
        }
    };

    // Shrink the navbar 
    navbarShrink();

    // Shrink the navbar when page is scrolled
    document.addEventListener('scroll', navbarShrink);

    // Activate Bootstrap scrollspy on the main nav element
    const mainNav = document.body.querySelector('#mainNav');
    if (mainNav) {
        new bootstrap.ScrollSpy(document.body, {
            target: '#mainNav',
            rootMargin: '0px 0px -40%',
        });
    };

    // Collapse responsive navbar when toggler is visible
    const navbarToggler = document.body.querySelector('.navbar-toggler');
    const responsiveNavItems = [].slice.call(
        document.querySelectorAll('#navbarResponsive .nav-link')
    );
    responsiveNavItems.map(function (responsiveNavItem) {
        responsiveNavItem.addEventListener('click', () => {
            if (window.getComputedStyle(navbarToggler).display !== 'none') {
                navbarToggler.click();
            }
        });
    });

    // Activate SimpleLightbox plugin for portfolio items
    new SimpleLightbox({
        elements: '#portfolio a.portfolio-box'
    });

    // Check for token and fetch data
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    fetch('http://localhost:3000/api/data', {
        headers: { 'x-access-token': token }
    })
    .then(response => response.json())
    .then(data => {
        const dataDiv = document.getElementById('data');
        if (dataDiv) {
            dataDiv.innerHTML = JSON.stringify(data, null, 2);
        }
    })
    .catch(error => {
        console.error('Error fetching data:', error);
    });
});

let popup = document.getElementById("popup");
let overlay = document.getElementById("overlay");

function openPopup() {
    popup.classList.add("open-popup");
    overlay.style.display = "block"; // Exibe a película
}

function closePopup() {
    popup.classList.remove("open-popup");
    overlay.style.display = "none"; // Esconde a película
}

function abrirMorador() {
    window.location.href = 'morador.html';
}
