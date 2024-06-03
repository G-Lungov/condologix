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
            localStorage.setItem('token', data.token);
            window.location.href = 'index.html';
        } else {
            alert('Login failed');
        }
    })
    .catch(error => console.error('Error:', error));
});
