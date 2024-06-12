window.addEventListener('DOMContentLoaded', event => {
    // Function to generate a random 4-digit number
    function generateRandomFourDigitNumber() {
        return Math.floor(1000 + Math.random() * 9000);
    }

    // Send message to the resident
    document.getElementById('contactForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const randomCode = generateRandomFourDigitNumber();
        const phoneNumber = document.getElementById('contactNumber').value;
        const recipientName = document.getElementById('name').value;
        const message = `Olá ${recipientName}, sua encomenda já chegou na portaria, favor apresentar o código a seguir para a retirada. Código: ${randomCode}. Até breve!`;

        fetch('https://condologix.com/send-whatsapp', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ to: phoneNumber, message: message })
        })
        .then(response => response.text())
        .then(data => {
            alert(data);

            // Fetch the token from local storage
            const token = localStorage.getItem('token');
            let userRole = null;

            if (token) {
                try {
                    const decodedToken = JSON.parse(atob(token.split('.')[1]));
                    userRole = decodedToken.role;
                } catch (error) {
                    console.error('Error decoding token:', error);
                    alert('Invalid token. Please log in again.');
                    window.location.href = 'login'; // Redirect to login if token is invalid
                    return;
                }
            }

            let redirectUrl = '/'; // Default to index
            switch (userRole) {
                case 'A':
                    redirectUrl = '/adm';
                    break;
                case 'C':
                    redirectUrl = '/porteiro';
                    break;
                case 'R':
                    redirectUrl = '/morador';
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
