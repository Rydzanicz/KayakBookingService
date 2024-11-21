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

app.post('/generate-invoice', async (req, res) => {
  try {
    const { buyerName, buyerAddressEmail, buyerAddress, buyerNip, products } = req.body;

    const response = await axios.post(
      'http://localhost:8080/generate-invoice',
      { buyerName, buyerAddressEmail, buyerAddress, buyerNip, products },
      { responseType: 'arraybuffer' }
    );

    const contentDisposition = response.headers['content-disposition'];
    const fileName = contentDisposition.split('filename=')[1].replace(/"/g, '');

    const filePath = path.join(__dirname, fileName);

    fs.writeFileSync(filePath, response.data);

    res.download(filePath, fileName, (err) => {
      if (err) {
        console.error('Błąd przy pobieraniu pliku:', err);
      }
      fs.unlinkSync(filePath);
    });

  } catch (error) {
    console.error('Błąd przy komunikacji z backendem:', error);
    res.status(500).send('Wystąpił błąd przy generowaniu PDF.');
  }
});

app.listen(3000, () => {
  console.log('Server is running on http://localhost:3000');
});
