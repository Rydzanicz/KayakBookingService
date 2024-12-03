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

app.post('/save-invoice', async (req, res) => {
  try {
    const { buyerName, buyerAddressEmail, buyerAddress, buyerNip, orders } = req.body;

    const response = await axios.post('http://localhost:8080/save-invoice', {
      buyerName,
      buyerAddressEmail,
      buyerAddress,
      buyerNip,
      orders
    });

    if (response.status === 200) {
      res.status(200).send('Faktura została zapisana pomyślnie.');
    } else {
      res.status(response.status).send(response.data || 'Wystąpił błąd podczas zapisywania faktury.');
    }
  } catch (error) {
    console.error('Błąd podczas komunikacji z backendem:', error);
    res.status(500).send('Wystąpił błąd przy zapisie faktury.');
  }
});

app.post('/generate-invoice', async (req, res) => {
  try {
    const { invoiceId, addressEmail } = req.body;

    const response = await axios.post(
      `http://localhost:8080/generate-invoice?invoiceId=${invoiceId || ''}`,
      {},
      { responseType: 'arraybuffer' }
    );

    const contentDisposition = response.headers['content-disposition'];
    const fileName = contentDisposition.split('filename=')[1].replace(/"/g, '');

    res.setHeader('Content-Disposition', `attachment; filename=${fileName}`);
    res.setHeader('Content-Type', 'application/pdf');
    res.status(200).send(response.data);
  } catch (error) {
    console.error('Błąd przy generowaniu faktury:', error.message);
    res.status(500).send('Błąd generowania faktury.');
  }
});

app.listen(3000, () => {
  console.log('Serwer działa na porcie 3000');
});
