require('dotenv').config();
const express = require('express');
const sql = require('mssql');
const cors = require('cors');
const accountRoutes = require('./routes/accounts');

const app = express();
app.use(cors());
app.use(express.json());

// Database configuration
const dbConfig = {
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    server: process.env.DB_SERVER,
    database: process.env.DB_DATABASE,
    options: {
        encrypt: false,
        trustServerCertificate: true
    }
};

// Connect to SQL Server
sql.connect(dbConfig).then(pool => {
    console.log('Connected to SQL Server');
    return pool;
}).catch(err => {
    console.error('SQL Server connection error:', err);
});

// Mount routes
app.use('/accounts', accountRoutes);

// Start server
app.listen(3000, () => console.log('Server running on http://localhost:3000'));