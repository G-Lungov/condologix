document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const USER_NAME_EMAIL = document.getElementById("userNameEmail").value;
    const USER_PASSWORD = document.getElementById("userPassword").value;

    fetch('http://localhost:3000/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ USER_NAME_EMAIL, USER_PASSWORD })
    })
    .then(response => response.json())
    .then(data => {
        if (data.auth && data.token) {
            // Save the token in localStorage
            localStorage.setItem('token', data.token);
            
            // Determine the redirection URL based on the user's role
            let redirectUrl = '';
            switch (data.role) {
                case 'A':
                    redirectUrl = 'https://condologix.com/login/administrator'; // Admin page without .html
                    break;
                case 'C':
                    redirectUrl = 'https://condologix.com/login/concierge/'; // Concierge page without .html
                    break;
                case 'R':
                    redirectUrl = 'https://condologix.com/login/resident/'; // Resident page without .html
                    break;
                default:
                    alert('Role not recognized');
                    return;
            }

            // Redirect to the appropriate page
            window.location.href = redirectUrl;
        } else {
            alert('Login failed: ' + (data.message || 'Please check your credentials and try again.'));
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('An error occurred during login. Please try again later.');
    });
});
