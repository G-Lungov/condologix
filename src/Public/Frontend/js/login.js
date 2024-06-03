document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    fetch('http://localhost:3000/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
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
