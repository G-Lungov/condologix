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
console.log('User database: ', userDb);

function fetchPackageContent() {
    const jsonData = {
        database: userDb
    };
    fetch('/package-historic', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(jsonData)
    })
    .then(response => response.json())
    .then(data => {
        console.log('Package content: ', data);
        displayPackageContent(data);
    })
    .catch(error => console.error('Error fetching package content: ', error));
}

function displayPackageContent(data) {
    const table = document.createElement('table');
    const headerRow = document.createElement('tr');
    
    // Mapeamento dos nomes das colunas
    const headers = {
        'ID_PACKAGE': 'Nº Pacote',
        'PACKAGE_SENDER_NAME': 'Remetente',
        'PACKAGE_ARRIVAL_DATE': 'Chegada',
        'CONCIERGE_NAME': 'Porteiro(a)'
    };

    // Criação dos cabeçalhos da tabela
    Object.values(headers).forEach(headerText => {
        const header = document.createElement('th');
        header.textContent = headerText;
        headerRow.appendChild(header);
    });
    table.appendChild(headerRow);
    
    // Criação das linhas da tabela com os dados
    data.forEach(item => {
        const row = document.createElement('tr');
        Object.keys(headers).forEach(header => {
            const cell = document.createElement('td');
            cell.textContent = item[header];
            row.appendChild(cell);
        });
        table.appendChild(row);
    });

    // Adiciona a tabela ao DOM
    const contentDiv = document.getElementById('package-content');
    contentDiv.innerHTML = ''; // Limpa o conteúdo anterior
    contentDiv.appendChild(table);
}

fetchPackageContent();
