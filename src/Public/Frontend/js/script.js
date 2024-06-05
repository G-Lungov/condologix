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

    // Decode the JWT token to get user role
    function parseJwt(token) {
        var base64Url = token.split('.')[1];
        var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    }

    const decodedToken = parseJwt(token);
    const userRole = decodedToken.role;

    fetch('http://localhost:3000/api/data', {
        headers: { 'x-access-token': token }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok ' + response.statusText);
        }
        return response.json();
    })
    .then(data => {
        const dataDiv = document.getElementById('data');
        if (dataDiv) {
            dataDiv.innerHTML = JSON.stringify(data, null, 2);
        }
    })
    .catch(error => {
        console.error('Error fetching data:', error);
    });

    // Send message to the resident
    document.getElementById('contactForm').addEventListener('submit', function(e) {
        e.preventDefault();

        // Function to generate a random 4-digit number
        function generateRandomFourDigitNumber() {
            return Math.floor(1000 + Math.random() * 9000);
        }
        const randomCode = generateRandomFourDigitNumber();
        const phoneNumber = document.getElementById('contactNumber').value;
        const recipientName = document.getElementById('name').value;
        const message = `Olá ${recipientName}, sua encomenda já chegou na portaria, favor apresentar o código a seguir para a retirada. Código: ${randomCode}. Até breve!`

        fetch('http://localhost:3000/send-whatsapp', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ to: phoneNumber, message: message })
        })
        .then(response => response.text())
        .then(data => {
            alert(data);
            let redirectUrl = 'index.html';
            switch (userRole) {
                case 'A':
                    redirectUrl = 'adm.html';
                    break;
                case 'C':
                    redirectUrl = 'porteiro.html';
                    break;
                case 'R':
                    break;
                default:
                    alert('Role not recognized');
                    return;
            }
            window.location.href = redirectUrl;
        })
        .catch(error => alert('Error: ' + error));
    });
    
    const registerButton = document.getElementById('registerButton');
    if (registerButton) {
        registerButton.addEventListener('click', function(e) {
            document.getElementById('contactForm').dispatchEvent(new Event('submit'));
        });
    }
});

let popup = document.getElementById("popup");
let overlay = document.getElementById("overlay");

function openPopup() {
    popup.classList.add("open-popup");
    overlay.style.display = "block"; // Exibe a película
};

function closePopup() {
    popup.classList.remove("open-popup");
    overlay.style.display = "none"; // Esconde a película
};

function abrirAdm() {
    window.location.href = 'adm.html';
};

function abrirMorador() {
    window.location.href = 'morador.html';
};

function abrirPorteiro() {
    window.location.href = 'porteiro.html';
};
