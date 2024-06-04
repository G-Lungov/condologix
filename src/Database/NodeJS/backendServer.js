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

// Serve static files from the Frontend public directory
app.use(express.static(path.join(__dirname, '../../Public/Frontend')));

// Create a MySQL connection pool
const db = mysql.createPool({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME
});

// Test the MySQL connection
db.getConnection((err, connection) => {
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
  const sql = 'SELECT * FROM users WHERE USER_NAME_EMAIL = ?';
  db.query(sql, [USER_NAME_EMAIL], (err, results) => {
    if (err) {
      return res.status(500).send('Error on the server.');
    }
    if (results.length === 0) {
      return res.status(404).send('No user found.');
    }

    const user = results[0];
    const passwordIsValid = bcrypt.compareSync(USER_PASSWORD, user.USER_PASSWORD); // Adjusted password comparison
    if (!passwordIsValid) {
      return res.status(401).send({ auth: false, token: null, message: 'Invalid password' });
    }

    const token = jwt.sign({ id: user.id, role: user.USER_ROLE }, process.env.SECRET, {
      expiresIn: 86400 // 24 hours
    });

    res.status(200).send({ auth: true, token: token, role: user.USER_ROLE });
  });
});

// Middleware to verify token
function verifyToken(req, res, next) {
  const token = req.headers['x-access-token'];
  if (!token) {
    return res.status(403).send({ auth: false, message: 'No token provided.' });
  }

  jwt.verify(token, process.env.SECRET, (err, decoded) => {
    if (err) {
      return res.status(500).send({ auth: false, message: 'Failed to authenticate token.' });
    }
    req.userId = decoded.id;
    next();
  });
}

// Protected route to fetch data
app.get('/api/data', verifyToken, (req, res) => {
  const sql = 'SELECT * FROM your_table';
  db.query(sql, (err, results) => {
    if (err) {
      return res.status(500).send(err);
    }
    res.json(results);
  });
});

// Endpoint to send WhatsApp message
app.post('/send-whatsapp', (req, res) => {
  const { to, message } = req.body;

  client.messages.create({
    body: message,
    from: `whatsapp:${twiPhoneNumber}`,
    to: `whatsapp:${to}`
  })
  .then(message => res.status(200).send(`Message sent with SID: ${message.sid}`))
  .catch(error => res.status(500).send(error));
});

// Start the server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
