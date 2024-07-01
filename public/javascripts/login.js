document.addEventListener('DOMContentLoaded', () => {
    const submitButton = document.getElementById('submitButton');
    if (submitButton) {
        submitButton.addEventListener('click', function(event) {
            event.preventDefault();

            const USER_NAME_EMAIL = document.getElementById("userNameEmail").value;
            const USER_PASSWORD = document.getElementById("userPassword").value;

            const jsonData = {
                USER_NAME_EMAIL: USER_NAME_EMAIL,
                USER_PASSWORD: USER_PASSWORD
            };

            fetch('/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(jsonData)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok: ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                console.log('Response from server:', data);

                if (data.auth === true) {
                    // Armazenar o token no localStorage
                    localStorage.setItem('token', data.token);
                    
                    // Adicionar o token aos cabeçalhos das requisições subsequentes
                    const token = data.token;
                    
                    let redirectUrl = '';
                    switch (data.role) {
                        case 'A':
                            redirectUrl = '/administrator';
                            break;
                        case 'C':
                            redirectUrl = '/concierge';
                            break;
                        case 'R':
                            redirectUrl = '/resident';
                            break;
                        default:
                            alert('Role not recognized');
                            redirectUrl = 'https://condologix.com';
                            return;
                    }
                    // Redirecionar para a URL apropriada com o token no corpo da requisição
                    window.location.href = redirectUrl + `?token=${token}`;
                } else {
                    alert('Login failed: ' + (data.message || 'Please check your credentials and try again.'));
                }
            })
            .catch(error => {
                console.error('Error during login:', error);
                alert('An error occurred during login. Please try again later.');
            });
        });
    } else {
        console.error('Submit button not found');
    }
});
