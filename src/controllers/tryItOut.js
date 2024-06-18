window.addEventListener('DOMContentLoaded', event => {
    // Function to generate a random 4-digit number
    function generateRandomFourDigitNumber() {
        return Math.floor(1000 + Math.random() * 9000);
    }

    // Send message to the resident
    document.getElementById('contactForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const randomCode = generateRandomFourDigitNumber();
        const phoneNumber = document.getElementById('phone').value;
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
            let redirectUrl = '/'; // Default to index
            window.location.href = redirectUrl;
        })
        .catch(error => alert('Error: ' + error));
    });

    const sendButton = document.getElementById('sendButton');
    if (sendButton) {
        sendButton.addEventListener('click', function(e) {
            document.getElementById('contactForm').dispatchEvent(new Event('submit'));
        });
    }
});
