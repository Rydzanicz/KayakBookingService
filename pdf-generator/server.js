const express = require('express');
const fs = require('fs');
const path = require('path');
const axios = require('axios');
const app = express();

app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

app.use(express.urlencoded({ extended: true }));
app.use(express.json());

app.get('/', (req, res) => {
  res.render('form');
});

app.listen(3000, () => {
  console.log('Server is running on http://localhost:3000');
});
