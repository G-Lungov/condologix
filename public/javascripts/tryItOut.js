window.addEventListener('DOMContentLoaded', event => {
    // Function to generate a random 4-digit number
    function generateRandomFourDigitNumber() {
        return Math.floor(1000 + Math.random() * 9000);
    }

    // Send message to the resident
    const sendButton = document.getElementById('sendButton');
    if (sendButton) {
        sendButton.addEventListener('click', function(event) {
            event.preventDefault();

            const randomCode = generateRandomFourDigitNumber();
            const phoneNumber = document.getElementById('phone').value;
            const recipientName = document.getElementById('name').value;
            const clientMessage = `Olá ${recipientName}, sua encomenda já chegou na portaria, favor apresentar o código a seguir para a retirada. Código: ${randomCode}. Até breve!`;

            const jsonData = {
                message: clientMessage,
                to: phoneNumber
            };

            fetch('/try-it-out', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(jsonData)
            })
            .then(response => response.text())
            .then(data => {
                alert(data);
                let redirectUrl = 'https://condologix.com/'; // Default to index
                window.location.href = redirectUrl;
            })
            .catch(error => alert('Error: ' + error));
        });
    }
});
