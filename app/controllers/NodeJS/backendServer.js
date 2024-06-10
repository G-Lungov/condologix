// Import necessary modules
const express = require('express');
const mysql = require('mysql');
const dotenv = require('dotenv');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const path = require('path');
const twilio = require('twilio');

// Load environment variables from .env file
dotenv.config();

// Twilio credentials
const accountSid = process.env.TWILIO_ACCOUNT_SID;
const authToken = process.env.TWILIO_AUTH_TOKEN;
const twiPhoneNumber = process.env.TWILIO_PHONE_NUMBER;
const client = twilio(accountSid, authToken);

// Create an Express application
const app = express();
app.use(express.json()); // To parse JSON bodies

// Serve static files from the 'public' directory
app.use(express.static(path.join(__dirname, '../../public')));

// Create a MySQL connection pool for the main database
const mainDb = mysql.createPool({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME // Main database for user authentication
});

// Test the MySQL connection
mainDb.getConnection((err, connection) => {
  if (err) {
    console.error('Error connecting to the database:', err);
    return;
  }
  console.log('Connected to the MySQL database.');
  connection.release();
});

// User login endpoint
app.post('/api/login', (req, res) => {
  const { USER_NAME_EMAIL, USER_PASSWORD } = req.body;
  const sql = 'SELECT * FROM Users WHERE USER_NAME_EMAIL = ?';
  mainDb.query(sql, [USER_NAME_EMAIL], (err, results) => {
    if (err) {
      return res.status(500).send('Error on the server.');
    }
    if (results.length === 0) {
      return res.status(404).send('No user found.');
    }

    const user = results[0];
    const passwordIsValid = bcrypt.compareSync(USER_PASSWORD, user.USER_PASSWORD);
    if (!passwordIsValid) {
      return res.status(401).send({ auth: false, token: null, message: 'Invalid password' });
    }

    // Generate JWT token
    const token = jwt.sign(
      { id: user.ID_USER, role: user.USER_ROLE, database: user.USER_DB, schema: user.USER_SCHEMA },
      process.env.SECRET,
      { expiresIn: 86400 } // 24 hours
    );

    res.status(200).send({ auth: true, token: token, role: user.USER_ROLE, database: user.USER_DB });
  });
});

// Middleware to verify token and connect to the appropriate database/schema
function verifyTokenAndConnect(req, res, next) {
  const token = req.headers['x-access-token'];
  if (!token) {
    return res.status(403).send({ auth: false, message: 'No token provided.' });
  }

  jwt.verify(token, process.env.SECRET, (err, decoded) => {
    if (err) {
      return res.status(500).send({ auth: false, message: 'Failed to authenticate token.' });
    }

    req.userId = decoded.id;
    req.userRole = decoded.role;
    req.userDatabase = decoded.database;

    // Log the database name for debugging
    console.log('Connecting to database:', req.userDatabase);

    // Connect to the specific user's database/schema
    req.userDb = mysql.createPool({
      host: process.env.DB_HOST_CONDOMINUMS,
      user: process.env.DB_USER_CONDOMINUMS,
      password: process.env.DB_PASSWORD_CONDOMINUMS,
      database: req.userDatabase // The specific database or schema name from the token
    });

    // Test the connection to the user's database
    req.userDb.getConnection((err, connection) => {
      if (err) {
        console.error('Error connecting to the user\'s database:', err);
        return res.status(500).send('Error connecting to the user\'s database');
      }
      console.log(`Successfully connected to the user's database: ${req.userDatabase}`);
      connection.release();
      next();
    });
  });
}

// Middleware to check user role
function checkUserRole(allowedRoles) {
  return (req, res, next) => {
    const userRole = req.userRole; // role set in the verifyTokenAndConnect middleware
    if (!allowedRoles.includes(userRole)) {
      return res.status(403).send({ message: 'Access denied: insufficient permissions' });
    }
    next();
  };
}

// Routes with role-based access control

// Admin page route (adm.html), accessible only by users with role 'A'
app.get('/adm', verifyTokenAndConnect, checkUserRole(['A']), (req, res) => {
  res.sendFile(path.join(__dirname, '../../../adm', 'index'));
});

// Resident page route (morador.html), accessible only by users with role 'R'
app.get('/morador', verifyTokenAndConnect, checkUserRole(['R']), (req, res) => {
  res.sendFile(path.join(__dirname, '../../../morador', 'index'));
});

// Concierge page route (porteiro.html), accessible only by users with role 'C'
app.get('/porteiro', verifyTokenAndConnect, checkUserRole(['C']), (req, res) => {
  res.sendFile(path.join(__dirname, '../../../porteiro', 'index'));
});

// History page route (history.html), accessible to all authenticated users
app.get('/history', verifyTokenAndConnect, (req, res) => {
  res.sendFile(path.join(__dirname, '../../../history', 'index'));
});

// Test registration page route (teste-cadastro.html), accessible to all authenticated users
app.get('/teste-cadastro', verifyTokenAndConnect, (req, res) => {
  res.sendFile(path.join(__dirname, '../../../teste-cadastro', 'index'));
});

// Default route for the main index page
app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, '../../../', 'index'));
});

// Protected route to fetch data from the user's specific database/schema
app.get('/api/data', verifyTokenAndConnect, (req, res) => {
  // Implement your logic to fetch data here, for now, we'll just send a success message
  res.send('Data route accessed successfully.');
});

// Endpoint to send WhatsApp message
app.post('/send-whatsapp', (req, res) => {
  const { to, message } = req.body;

  client.messages.create({
    body: message,
    from: `whatsapp:${twiPhoneNumber}`, // Ensure this is your approved Twilio WhatsApp number
    to: `whatsapp:${to}` // Ensure the 'to' number is correctly formatted
  })
  .then(message => res.status(200).send(`Message sent with SID: ${message.sid}`))
  .catch(error => {
    console.error('Twilio Error:', error);
    res.status(500).send(error);
  });
});
