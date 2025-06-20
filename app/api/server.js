require('dotenv').config();
const express = require('express');
const sql = require('mssql');
const cors = require('cors');
const accountRoutes = require('./routes/accounts');
const campsiteRoutes = require('./routes/campsites');
const commentRoutes = require('./routes/comments');
const customerRoutes = require('./routes/customers');
const feedbackRoutes = require('./routes/feedback');
const gearRoutes = require('./routes/gears');
const notificationRoutes = require('./routes/notifications');
const orderDetailRoutes = require('./routes/orderDetails');
const orderRoutes = require('./routes/orders');
const ownerRoutes = require('./routes/owners');
const reportLocationRoutes = require('./routes/reportLocations');
const reportOwnerRoutes = require('./routes/reportOwners');
const userRoutes = require('./routes/users');
const voucherRoutes = require('./routes/vouchers');
const walletRoutes = require('./routes/wallets');

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
app.use('/campsites', campsiteRoutes);
app.use('/comments', commentRoutes);
app.use('/customers', customerRoutes);
app.use('/feedback', feedbackRoutes);
app.use('/gears', gearRoutes);
app.use('/notifications', notificationRoutes);
app.use('/orderDetails', orderDetailRoutes);
app.use('/orders', orderRoutes);
app.use('/owners', ownerRoutes);
app.use('/reportLocations', reportLocationRoutes);
app.use('/reportOwners', reportOwnerRoutes);
app.use('/users', userRoutes);
app.use('/vouchers', voucherRoutes);
app.use('/wallets', walletRoutes);

// Start server
app.listen(3000, () => console.log('Server running on http://localhost:3000'));