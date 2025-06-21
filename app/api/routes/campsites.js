const express = require('express');
const router = express.Router();
const sql = require('mssql');
require('dotenv').config();

// Kết nối cấu hình SQL từ biến môi trường
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

// Route GET /campsites
router.get('/', async (req, res) => {
    try {
        const pool = await sql.connect(dbConfig);
        const result = await pool.request().query(`
            SELECT
                Campsite_id AS campId,
                Price AS campPrice,
                Campsite_owner AS campOwner,
                Address AS campAddress,
                Name AS campName,
                Description AS campDescription,
                Image AS campImage,
                Status AS campStatus,
                Quantity AS limite
            FROM CAMPSITE
        `);
        res.json(result.recordset);
    } catch (err) {
        console.error('Lỗi lấy danh sách campsite:', err);
        res.status(500).send('Server error');
    }
});

module.exports = router;
