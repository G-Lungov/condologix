window.addEventListener('DOMContentLoaded', event => {
    // Determine the current page
    const currentPage = window.location.pathname.split('/').pop();

    // Pages that do not require token verification
    const noTokenPages = ['index.html', 'login.html'];

    // Check if the current page requires token verification
    if (!noTokenPages.includes(currentPage)) {
        // Check for token in localStorage
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = 'login.html';
            return;
        }

        // Decode the JWT token to get user information
        function parseJwt(token) {
            try {
                const base64Url = token.split('.')[1];
                const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
                const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
                    return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
                }).join(''));
                return JSON.parse(jsonPayload);
            } catch (error) {
                console.error('Error parsing token:', error);
                return null;
            }
        }

        const decodedToken = parseJwt(token);
        if (!decodedToken) {
            alert('Invalid token. Please log in again.');
            window.location.href = 'login.html';
            return;
        }

        const userRole = decodedToken.role;
        console.log('User role:', userRole); // For debugging

        // Fetch data from the protected route
        fetch('http://localhost:3000/api/data', {
            headers: { 'x-access-token': token }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            // Check if the response is JSON
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return response.json();
            } else {
                return response.text(); // Handle as text if not JSON
            }
        })
        .then(data => {
            const dataDiv = document.getElementById('data');
            if (dataDiv) {
                // Check if data is a string (text response)
                if (typeof data === 'string') {
                    dataDiv.innerHTML = data;
                } else {
                    dataDiv.innerHTML = JSON.stringify(data, null, 2);
                }
            }
        })
        .catch(error => {
            console.error('Error fetching data:', error);
            alert('Error fetching data: ' + error.message); // Display a more descriptive error
            // Optionally redirect to login or an error page
            window.location.href = 'login.html';
        });
    }

    // Common script logic for all pages

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
});

// Popup management functions
let popup = document.getElementById("popup");
let overlay = document.getElementById("overlay");

function openPopup() {
    popup.classList.add("open-popup");
    overlay.style.display = "block"; // Show the overlay
}

function closePopup() {
    popup.classList.remove("open-popup");
    overlay.style.display = "none"; // Hide the overlay
}

function abrirAdm() {
    window.location.href = 'adm.html';
}

function abrirMorador() {
    window.location.href = 'morador.html';
}

function abrirPorteiro() {
    window.location.href = 'porteiro.html';
}
