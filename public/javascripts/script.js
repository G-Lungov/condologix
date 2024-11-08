window.addEventListener('DOMContentLoaded', event => {
    // Determine the current page
    const currentPage = window.location.pathname.split('/').pop();

    // Pages that do not require token verification
    const noTokenPages = ['', 'login', 'try-it-out']; // Updated for cleaner URLs without .html

    // Define allowed roles for each page
    const pageRoles = {
        'administrator': ['A', 'S'],         // Only administrators
        'concierge': ['C', 'S'],             // Only concierges
        'resident': ['R', 'S'],              // Only residents
        'register': ['C', 'S'],              // Only concierges
        'package-historic': ['A', 'R', 'S'], // Only administrators & residents
        'support': ['A', 'R', 'S'],          // Only administrators & residents
        'update-data': ['A', 'R', 'S'],      // Only administrators & residents
    };

    // Check if the current page requires token verification
    if (!noTokenPages.includes(currentPage)) {
        // Check for token in localStorage
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = 'https://condologix.com/login';
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
            window.location.href = 'https://condologix.com/login';
            return;
        }

        const userRole = decodedToken.role;
        console.log('User role:', userRole); // For debugging

        // Check if the user has access to the current page
        if (pageRoles[currentPage] && !pageRoles[currentPage].includes(userRole)) {
            alert('You do not have permission to access this page.');
            window.location.href = '/login'; // Redirect to login page
            return;
        }

        // Hide loading screen and show the main content if token is valid and user has permission
        const loadingScreen = document.getElementById('loadingScreen');
        const mainContent = document.getElementById('mainContent');
        if (loadingScreen) loadingScreen.style.display = 'none';
        if (mainContent) mainContent.style.display = 'block';
        
    } else {
        // Hide loading screen and show the main content if the page doesn't require token verification
        const loadingScreen = document.getElementById('loadingScreen');
        const mainContent = document.getElementById('mainContent');
        if (loadingScreen) loadingScreen.style.display = 'none';
        if (mainContent) mainContent.style.display = 'block';
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
