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
            const token = localStorage.getItem('token');
            const userRole = token ? JSON.parse(atob(token.split('.')[1])).role : null;
            let redirectUrl = 'index.html';
            switch (userRole) {
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
