// backendServer.js
const express = require('express');
const mysql = require('mysql');
const dotenv = require('dotenv');

// Path import
const path = require('path')

// Load environment variables from .env file
dotenv.config();

// Create an Express application
const app = express();

// Serve static files from the Frontend directory
app.use(express.static(path.join(__dirname, '../../Frontend')));

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
  
// Define a simple route to fetch data from the database
app.get('/api/data', (req, res) => {
  const sql = 'SELECT * FROM your_table';
  db.query(sql, (err, results) => {
    if (err) {
      res.status(500).send(err);
    } else {
      res.json(results);
    }
  });
});

// Start the server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
