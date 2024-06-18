document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const USER_NAME_EMAIL = document.getElementById("userNameEmail").value;
    const USER_PASSWORD = document.getElementById("userPassword").value;

    fetch('/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ USER_NAME_EMAIL, USER_PASSWORD })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.statusText);
        }
        return response.json();
    })
    .then(data => {
        if (data.auth && data.token) {
            localStorage.setItem('token', data.token);
            
            let redirectUrl = '';
            switch (data.role) {
                case 'A':
                    redirectUrl = '/login/administrator';
                    break;
                case 'C':
                    redirectUrl = '/login/concierge';
                    break;
                case 'R':
                    redirectUrl = '/login/resident';
                    break;
                default:
                    alert('Role not recognized');
                    return;
            }
            window.location.href = redirectUrl;
        } else {
            alert('Login failed: ' + (data.message || 'Please check your credentials and try again.'));
        }
    })
    .catch(error => {
        console.error('Error during login:', error);
        alert('An error occurred during login. Please try again later.');
    });
});
