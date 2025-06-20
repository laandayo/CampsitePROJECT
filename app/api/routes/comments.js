const express = require('express');
const sql = require('mssql');
const router = express.Router();

const dbConfig = {
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    server: process.env.DB_SERVER,
    database: process.env.DB_DATABASE,
    options: { encrypt: false, trustServerCertificate: true }
};

router.get('/', async (req, res) => {
    try {
        let pool = await sql.connect(dbConfig);
        let result = await pool.request().query('SELECT * FROM dbo.COMMENT');
        res.json(result.recordset);
    } catch (err) {
        console.error('Database query error:', err);
        res.status(500).json({ error: 'Failed to retrieve comments: ' + err.message });
    }
});

module.exports = router;