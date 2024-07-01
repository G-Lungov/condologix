// <REQUIREMENTS> //
// Dependencies
const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const logger = require('morgan');
const mysql = require('mysql');
const dotenv = require('dotenv');
const jwt = require('jsonwebtoken');
const twilio = require('twilio');

// Requiring routes
const indexRouter = require('./routes/index');
const tryItOutRouter = require('./routes/try-it-out');
const loginRouter = require('./routes/login');
const administratorRouter = require('./routes/administrator');
const residentRouter = require('./routes/resident');
const conciergeRouter = require('./routes/concierge');
const registerRouter = require('./routes/register');
const packageHistoricRouter = require('./routes/package-historic');
const supportRouter = require('./routes/support');
const updateDataRouter = require('./routes/update-data');
// <REQUIREMENTS> //


// <CONFIGURATION> //
// Load environment variables from .env file
dotenv.config();

// Create Express application
const app = express();

// Set view engine
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// Middleware to interpret JSON in requests
app.use(express.json());

// Serve static files from 'public_html' directory
app.use(express.static(path.join(__dirname)));

// Middleware for logging
app.use(logger('dev'));

// Middleware to parse cookies from HTTP requests
app.use(cookieParser());
// <CONFIGURATION> //


// <FUNCTIONS> //
// Verify if passwords are equal
const passwordIsValid = (password1, password2) => {
  if (password1 === password2) {
    console.log('Password is valid');
    return true; // Passwords are equal
  } else {
    console.log('Invalid password');
    return false; // Passwords are not equal
  }
};

// Create a connection pool to the main database
const mainDb = mysql.createPool({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME // Main database for user authentication
});

// Test the connection to MySQL
mainDb.getConnection((err, connection) => {
  if (err) {
    console.error('Error connecting to the database:', err.message);
    console.error('Stack trace:', err.stack);
    return;
  }
  console.log('Connected to the MySQL database.');
  connection.release();
});

// Twilio credentials
const accountSid = process.env.TWILIO_ACCOUNT_SID;
const authToken = process.env.TWILIO_AUTH_TOKEN;
const twiPhoneNumber = process.env.TWILIO_PHONE_NUMBER;
const client = twilio(accountSid, authToken);
// <FUNCTIONS> //


// <MIDDLEWARES> //
// <MIDDLEWARES> //


// <ROUTES> //
// Routes for non-token verified access
app.use('/', indexRouter);
app.use('/try-it-out', tryItOutRouter);
app.use('/login', loginRouter);

// Routes with role-based access control
app.use('/administrator', administratorRouter);
app.use('/concierge', conciergeRouter);
app.use('/resident', residentRouter);
app.use('/register', registerRouter);
app.use('/package-historic', packageHistoricRouter);
app.use('/support', supportRouter);
app.use('/update-data', updateDataRouter);
// <ROUTES> //


// <ENDPOINTS> //
// User login endpoint
app.post('/login', (req, res) => {
  const { USER_NAME_EMAIL, USER_PASSWORD } = req.body;
  console.log('Received login data:', req.body);
  console.log('Login request received for:', USER_NAME_EMAIL);

  const sql = 'SELECT * FROM USERS WHERE USER_NAME_EMAIL = ?';
  mainDb.query(sql, [USER_NAME_EMAIL], (err, results) => {
    if (err) {
      console.error('Error executing query:', err);
      return res.status(500).send('Error on the server.');
    }
    if (results.length === 0) {
      console.log('No user found for:', USER_NAME_EMAIL);
      return res.status(404).send('No user found.');
    }
    const userMain = results[0];
    console.log('User found:', userMain.USER_NAME_EMAIL);

    // Validate password
    if (passwordIsValid(USER_PASSWORD, userMain.USER_PASSWORD)) {
      // Generate JWT token
      const token = jwt.sign(
        { id: userMain.ID_USER, role: userMain.USER_ROLE, database: userMain.USER_DB },
        process.env.SECRET,
        { expiresIn: 86400 } // 24 hours
      );
      console.log('Login successful, token generated:', token);

      // Response in JSON
      res.json({
        auth: true,
        token: token,
        role: userMain.USER_ROLE
      });
      // Create a connection pool to the users database
      const userDb = mysql.createPool({
        host: process.env.DB_HOST,
        user: process.env.DB_USER,
        password: process.env.DB_PASSWORD,
        database: userMain.USER_DB // User database for connection
      });
      // Test connection to the database
      userDb.getConnection((err, connection) => {
        if (err) {
          console.error('Error connecting to the user\'s database:', err);
          return res.status(500).send('Error connecting to the user\'s database');
        }
        console.log(`Successfully connected to the user's database: ${userMain.USER_DB}`);
        connection.release();
      });
    } else {
      console.log('Invalid password for user:', USER_NAME_EMAIL);
      return res.status(401).json({
        auth: false,
        message: 'Invalid password'
      });
    }
  });
});

// Endpoint to send WhatsApp message
app.post('/try-it-out', (req, res) => {
  const { to, message } = req.body;

  client.messages.create({
      body: message,
      from: `whatsapp:${twiPhoneNumber}`,
      to: `whatsapp:${to}`
  })
  .then(message => {
      console.log('Message sent:', message.sid);
      res.status(200).send(`Message sent successfully to ${to}`);
  })
  .catch(error => {
      console.error('Twilio Error:', error);
      res.status(500).send('Failed to send message');
  });
});

// <ENDPOINTS> //


// Export the Express application
module.exports = app;
