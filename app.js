// <REQUIREMENTS> //
// Dependencies
const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const logger = require('morgan');
const mysql = require('mysql');
const dotenv = require('dotenv');
const jwt = require('jsonwebtoken');

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
  console.log('MySQL database working correctly.');
  connection.release();
});


// Format date to DD/MM/YYYY
function formatDate(date) {
  const d = new Date(date);
  let day = d.getDate();
  let month = d.getMonth() + 1; // Mês começa em 0
  const year = d.getFullYear();

  if (day < 10) {
    day = '0' + day;
  }
  if (month < 10) {
    month = '0' + month;
  }

  return `${day}/${month}/${year}`;
}
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
// User login
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

        const query = `SELECT * FROM USERS WHERE USER_NAME_EMAIL = ?`;
        userDb.query(query, [userMain.USER_NAME_EMAIL], (err, results) => {
          if (err) {
            console.error('Error executing query:', err);
            return res.status(500).send('Error on the server.');
          }
          if (results.length === 0) {
            console.log('No user found for:', userMain.USER_NAME_EMAIL);
            return res.status(404).send('No user found.');
          }

          const userData = results[0];

          // Generate JWT token
          const token = jwt.sign(
            { id: userData.ID_USER, role: userMain.USER_ROLE, database: userMain.USER_DB, email: userData.USER_NAME_EMAIL },
            process.env.SECRET,
            { expiresIn: 86400 } // 24 hours
          );
          console.log('Login successful, token generated:', token);

          // Response in JSON
          return res.json({
            auth: true,
            token: token,
            role: userMain.USER_ROLE
          });
        });
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

// Get data to register page
app.post('/register-data', (req, res) => {
  const jsonData = req.body
  const userDb = mysql.createPool({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: jsonData.database
  });
  const query = `
    SELECT RESIDENT_NAME, TERMINAL_BLOCK, TERMINAL_NUMBER 
    FROM RESIDENT
  `;

  userDb.query(query, (error, results) => {
    if (error) {
      console.error('Error fetching resident data: ', error);
      return res.status(500).send('Error fetching resident data');
    } else {
      // Format the data to group apartments by block
      const data = {
        residents: [],
        blocks: {},
      };

      results.forEach(row => {
        data.residents.push(row.RESIDENT_NAME);

        if (!data.blocks[row.TERMINAL_BLOCK]) {
          data.blocks[row.TERMINAL_BLOCK] = [];
        }
        data.blocks[row.TERMINAL_BLOCK].push(row.TERMINAL_NUMBER);
      });

      res.json(data);
    }
  });
});

// Register new package
app.post('/register', (req, res) => {
  const jsonData = req.body;

  const userDb = mysql.createPool({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: jsonData.database
  });

  const findResidentId = `
    SELECT ID_RESIDENT FROM RESIDENT 
    WHERE RESIDENT_NAME = ? AND TERMINAL_BLOCK = ? AND TERMINAL_NUMBER = ?
  `;
  const findConciergeId = `
  SELECT ID_CONCIERGE FROM CONCIERGE 
  WHERE ID_USER = ?
  `;

  userDb.query(findResidentId, [jsonData.residentName, jsonData.block, jsonData.apartment], (error, results) => {
    if (error) {
      console.error('Error finding resident: ', error);
      return res.status(500).send('Error finding resident');
    } else if (results.length === 0) {
      return res.status(404).send('Resident not found');
    } else {
      const residentId = results[0].ID_RESIDENT;
      userDb.query(findConciergeId, [jsonData.id], (error, results) => {
        if (error) {
          console.error('Error finding concierge: ', error);
          return res.status(500).send('Error finding concierge');
        } else if (results.length === 0) {
          return res.status(404).send('Concierge not found');
        } else {
          const conciergeId = results[0].ID_CONCIERGE;
          const insertPackageQuery = `
            INSERT INTO PACKAGE (PACKAGE_SENDER_NAME, PACKAGE_ARRIVAL_DATE, ID_RESIDENT, ID_CONCIERGE) 
            VALUES (?, NOW(), ?, ?)
          `;
          const dataToInsert = ['Remetente', residentId, conciergeId]; // Adjust as necessary
          userDb.query(insertPackageQuery, dataToInsert, (error, results) => {
            if (error) {
              console.error('Error inserting data: ', error);
              return res.status(500).send('Error inserting data');
            } else {
              res.status(201).send('Package registered successfully');
            }
          });
        }
      });
    }
  });
});

// Get package historic
app.post('/package-historic', (req, res) => {
  const jsonData = req.body;
  const userDb = mysql.createPool({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: jsonData.database 
  });
  const findResidentId = `SELECT ID_RESIDENT FROM RESIDENT WHERE ID_USER = ?`;
  userDb.query(findResidentId, [jsonData.id], (error, results) => {
    if (error) {
      console.error('Error finding resident: ', error);
      return res.status(500).send('Error finding resident');
    } else if (results.length === 0) {
      return res.status(404).send('Resident not found');
    } else {
      const residentId = results[0].ID_RESIDENT;
      const query = `
      SELECT
        p.ID_PACKAGE,
        p.PACKAGE_SENDER_NAME,
        p.PACKAGE_ARRIVAL_DATE,
        c.CONCIERGE_NAME
      FROM
        PACKAGE p
      JOIN
        CONCIERGE c,
        RESIDENT r
      ON
        p.ID_CONCIERGE = c.ID_CONCIERGE,
        p.ID_RESIDENT = r.ID_RESIDENT
      WHERE
        p.ID_RESIDENT = ?
      `;
      userDb.query(query, [residentId], (error, results) => {
        if (error) {
          console.error('Error fetching package historic: ', error);
          res.status(500).send('Internal Server Error');
        } else {
          const formattedResults = results.map(item => {
            return {
              ...item,
              PACKAGE_ARRIVAL_DATE: formatDate(item.PACKAGE_ARRIVAL_DATE),
              PACKAGE_PICKUP_DATE: formatDate(item.PACKAGE_PICKUP_DATE)
            };
          });
          res.json(formattedResults);
        }
      });
    }
  });
});

// To send WhatsApp message (Test page) - Will be replaced with WhatsApp Web API
app.post('/try-it-out', (req, res) => {
  const { to, message } = req.body;
  
  // TODO: Implement WhatsApp Web API integration
  res.status(501).send('WhatsApp Web API integration pending');
});

// <ENDPOINTS> //


// Export the Express application and Router
module.exports = app;
