const express = require('express');
const sql = require('mssql');
const router = express.Router();
const crypto = require('crypto');

router.post('/', async (req, res) => {
    try {
        let { firebase_uid, Gmail, first_name, last_name, phone_number, isAdmin, isOwner, passwordHash, deactivated } = req.body;
        if (!firebase_uid || !Gmail) {
            return res.status(400).json({ error: 'firebase_uid and Gmail are required.' });
        }
        // Hash the password
        if (passwordHash) {
            passwordHash = crypto.createHash('sha256').update(passwordHash).digest('hex');
        }
        let pool = await sql.connect(dbConfig);

        let result = await pool.request()
            .input('firebase_uid', sql.NVarChar, firebase_uid)
            .input('Gmail', sql.NVarChar, Gmail)
            .input('first_name', sql.NVarChar, first_name)
            .input('last_name', sql.NVarChar, last_name)
            .input('phone_number', sql.NVarChar, phone_number)
            .input('isAdmin', sql.Bit, isAdmin)
            .input('isOwner', sql.Bit, isOwner)
            .input('passwordHash', sql.NVarChar, passwordHash)
            .input('deactivated', sql.Bit, deactivated)
            .query(`
                IF NOT EXISTS (SELECT 1 FROM dbo.ACCOUNT WHERE firebase_uid = @firebase_uid)
                INSERT INTO dbo.ACCOUNT (firebase_uid, Gmail, first_name, last_name, phone_number, isAdmin, isOwner, passwordHash, deactivated)
                VALUES (@firebase_uid, @Gmail, @first_name, @last_name, @phone_number, @isAdmin, @isOwner, @passwordHash, @deactivated)
                SELECT * FROM dbo.ACCOUNT WHERE firebase_uid = @firebase_uid
            `);
        res.status(result.rowsAffected[0] > 0 ? 201 : 200).json(result.recordset[0]);
    } catch (err) {
        console.error('Database query error:', err);
        res.status(500).json({ error: 'Failed to create account: ' + err.message });
    }
});

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
        const { firebase_uid } = req.query;
        let query = 'SELECT * FROM ACCOUNT';
        if (firebase_uid) {
            query += ' WHERE firebase_uid = @firebase_uid';
        }
        let request = pool.request();
        if (firebase_uid) {
            request.input('firebase_uid', sql.NVarChar, firebase_uid);
        }
        let result = await request.query(query);
        res.json(result.recordset);
    } catch (err) {
        console.error('Database query error:', err);
        res.status(500).json({ error: 'Failed to retrieve accounts: ' + err.message });
    }
});

router.post('/', async (req, res) => {
    try {
        const { firebase_uid, Gmail, first_name, last_name, phone_number, isAdmin, isOwner, passwordHash, deactivated } = req.body;
        if (!firebase_uid || !Gmail) {
            return res.status(400).json({ error: 'firebase_uid and Gmail are required.' });
        }
        let pool = await sql.connect(dbConfig);
        let result = await pool.request()
            .input('firebase_uid', sql.NVarChar, firebase_uid)
            .input('Gmail', sql.NVarChar, Gmail)
            .input('first_name', sql.NVarChar, first_name)
            .input('last_name', sql.NVarChar, last_name)
            .input('phone_number', sql.NVarChar, phone_number)
            .input('isAdmin', sql.Bit, isAdmin)
            .input('isOwner', sql.Bit, isOwner)
            .input('passwordHash', sql.NVarChar, passwordHash)
            .input('deactivated', sql.Bit, deactivated)
            .query(`
                IF NOT EXISTS (SELECT 1 FROM dbo.ACCOUNT WHERE firebase_uid = @firebase_uid)
                INSERT INTO dbo.ACCOUNT (firebase_uid, Gmail, first_name, last_name, phone_number, isAdmin, isOwner, passwordHash, deactivated)
                VALUES (@firebase_uid, @Gmail, @first_name, @last_name, @phone_number, @isAdmin, @isOwner, @passwordHash, @deactivated)
                SELECT * FROM dbo.ACCOUNT WHERE firebase_uid = @firebase_uid
            `);
        res.status(result.rowsAffected[0] > 0 ? 201 : 200).json(result.recordset[0]);
    } catch (err) {
        console.error('Database query error:', err);
        res.status(500).json({ error: 'Failed to create account: ' + err.message });
    }
});

module.exports = router;