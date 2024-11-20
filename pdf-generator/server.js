const express = require('express');
const fs = require('fs');
const path = require('path');
const axios = require('axios'); // Zainstaluj axios do wysyłania zapytań HTTP
const app = express();

app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

// Middleware do parsowania formularzy
app.use(express.urlencoded({ extended: true }));

// Strona z formularzem
app.get('/', (req, res) => {
  res.render('form');
});

// Endpoint generujący PDF
app.post('/generate-pdf', async (req, res) => {
  try {
    const {
      sellerName, sellerAddress, sellerNip, sellerBankAccount, buyerName,
      buyerAddress, buyerNip, invoiceDate, saleDate, invoiceNumber, description,
      quantity, unitPrice, vatRate
    } = req.body;

    // Wysyłamy dane na endpoint w aplikacji Java
    const response = await axios.post('http://localhost:8080/generate-pdf', {
      sellerName,
      sellerAddress,
      sellerNip,
      sellerBankAccount,
      buyerName,
      buyerAddress,
      buyerNip,
      invoiceDate,
      saleDate,
      invoiceNumber,
      description,
      quantity,
      unitPrice,
      vatRate
    }, {
      responseType: 'arraybuffer' // Pobieramy dane binarne (plik PDF)
    });

    // Ustalamy ścieżkę pliku do zapisania na dysku
    const filePath = path.join(__dirname, `${invoiceNumber}.pdf`);
    fs.writeFileSync(filePath, response.data); // Zapisujemy plik PDF na dysku

    // Zwracamy plik PDF do pobrania
    res.download(filePath, `${invoiceNumber}.pdf`, (err) => {
      if (err) {
        console.error('Błąd przy pobieraniu pliku:', err);
      }
      // Usuwamy plik po pobraniu
      fs.unlinkSync(filePath);
    });
  } catch (error) {
    console.error('Błąd przy komunikacji z serwerem Java:', error);
    res.status(500).send('Wystąpił błąd podczas generowania PDF.');
  }
});

// Uruchomienie serwera Express
app.listen(3000, () => {
  console.log('Server is running on http://localhost:3000');
});
