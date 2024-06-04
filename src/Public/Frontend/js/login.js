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
            let redirectUrl = 'index.html';
            switch (data.role) {
                case 'A':
                    redirectUrl = 'adm.html';
                    break;
                case 'C':
                    redirectUrl = 'porteiro.html';
                    break;
                case 'R':
                    redirectUrl = 'morador.html';
                    break;
                default:
                    alert('Role not recognized');
                    return;
            }
            window.location.href = redirectUrl;
        } else {
            alert('Login failed');
        }
    })
    .catch(error => console.error('Error:', error));
});
