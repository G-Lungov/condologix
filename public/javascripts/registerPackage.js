// Check for token in localStorage
const token = localStorage.getItem('token');
if (!token) {
    window.location.href = 'https://condologix.com/login';
}

// Decode the JWT token to get user information
function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (error) {
        console.error('Error parsing token:', error);
        return null;
    }
}

const decodedToken = parseJwt(token);
if (!decodedToken) {
    alert('Invalid token. Please log in again.');
    window.location.href = 'https://condologix.com/login';
}

const userDb = decodedToken.database;
const userId = decodedToken.id;

document.addEventListener('DOMContentLoaded', () => {
    const residentNameInput = document.getElementById('residentName');
    const terminalBlockSelect = document.getElementById('terminalBlock');
    const terminalNumberSelect = document.getElementById('terminalNumber');
    const registerButton = document.getElementById('registerButton');

    // Função para preencher os dropdowns com os dados do banco de dados
    function populateFormData() {
        const jsonData = {
            database: userDb
        };

        fetch('/register-data', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify(jsonData)
        })
        .then(response => response.json())
        .then(data => {
            // Preencher nomes dos residentes
            residentNameInput.innerHTML = '<option value="">Selecione o Nome do Residente</option>';
            data.residents.forEach(name => {
                const option = document.createElement('option');
                option.value = name;
                option.textContent = name;
                residentNameInput.appendChild(option);
            });

            // Preencher blocos
            terminalBlockSelect.innerHTML = '<option value="">Selecione o Bloco</option>';
            for (const block in data.blocks) {
                const option = document.createElement('option');
                option.value = block;
                option.textContent = block;
                terminalBlockSelect.appendChild(option);
            }

            // Atualizar apartamentos com base no bloco selecionado
            terminalBlockSelect.addEventListener('change', () => {
                const selectedBlock = terminalBlockSelect.value;
                terminalNumberSelect.innerHTML = '<option value="">Selecione o Apartamento</option>';
                if (selectedBlock && data.blocks[selectedBlock]) {
                    data.blocks[selectedBlock].forEach(apartment => {
                        const option = document.createElement('option');
                        option.value = apartment;
                        option.textContent = apartment;
                        terminalNumberSelect.appendChild(option);
                    });
                }
            });
        })
        .catch(error => console.error('Error fetching form data: ', error));
    }

    // Função para enviar o formulário
    registerButton.addEventListener('click', (event) => {
        event.preventDefault();
        const jsonData = {
            residentName: residentNameInput.value,
            block: terminalBlockSelect.value,
            apartment: terminalNumberSelect.value,
            id: userId,
            database: userDb
        };

        fetch('/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify(jsonData)
        })
        .then(response => {
            if (response.ok) {
                alert('Pacote cadastrado com sucesso!');
                location.reload();
            } else {
                alert('Erro ao cadastrar pacote.');
            }
        })
        .catch(error => console.error('Error registering package: ', error));
    });

    // Preencher os dropdowns ao carregar a página
    populateFormData();
});