const express = require('express');
const router = express.Router();
const sql = require('mssql');
require('dotenv').config();

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

// Route GET /gears
router.get('/', async (req, res) => {
    try {
        const pool = await sql.connect(dbConfig);
        const result = await pool.request().query(`
            SELECT
                Gear_id AS gearId,
                Campsite_owner AS gearOwner,
                Price AS gearPrice,
                Name AS gearName,
                Description AS gearDescription,
                Image AS gearImage
            FROM GEAR
        `);
        res.json(result.recordset);
    } catch (err) {
        console.error('❌ Lỗi lấy danh sách gear:', err.message);
        res.status(500).send('Server error');
    }
});

module.exports = router;
