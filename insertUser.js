const mysql = require('mysql');
const bcrypt = require('bcryptjs');
const dotenv = require('dotenv');

// Load environment variables from .env file
dotenv.config();

// Create a MySQL connection pool
const db = mysql.createPool({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME
});

// Function to hash the password
const hashPassword = async (password) => {
  const salt = await bcrypt.genSalt(10);
  const hashedPassword = await bcrypt.hash(password, salt);
  return hashedPassword;
};

// Function to insert a new user into the database
const insertUser = async (id, username, password, role, userDb) => {
  try {
    const hashedPassword = await hashPassword(password);
    const sql = 'INSERT INTO USERS (ID_USER, USER_NAME_EMAIL, USER_PASSWORD, USER_ROLE, USER_DB) VALUES (?, ?, ?, ?, ?)';
    db.query(sql, [id, username, hashedPassword, role, userDb], (err, result) => {
      if (err) {
        console.error('Error inserting user:', err);
      } else {
        console.log('User inserted successfully:', result);
      }
    });
  } catch (error) {
    console.error('Error hashing password:', error);
  }
};

// Usage example
insertUser(0, 'gabriel@wedevelops.com', 'Lungov2001', 'A', 'condologix_03045002vldr');
