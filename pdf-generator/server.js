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

    // Pobranie nazwy pliku z nagłówka
    const contentDisposition = response.headers['content-disposition'];
    const fileName = contentDisposition.split('filename=')[1].replace(/"/g, '');

    // Wydzielenie numeru faktury i roku z nazwy pliku (np. FV/000000001/2024.pdf)
    const [invoiceId, year] = fileName.split('/').slice(0, -1); // Pobieramy numer faktury i rok
    const invoiceNumber = invoiceId.split('-')[1];  // Numery faktury: FV-000000001 -> 000000001

    // Ustalenie ścieżki do pliku, zgodnie z wymaganym formatem
    const filePath = path.join(__dirname, 'Faktura-FV', invoiceNumber, year, fileName);

    // Utworzenie folderów, jeśli nie istnieją
    const dirPath = path.dirname(filePath);
    if (!fs.existsSync(dirPath)) {
      fs.mkdirSync(dirPath, { recursive: true });
    }

    // Zapisanie pliku PDF na dysku
    fs.writeFileSync(filePath, response.data);

    // Wysłanie pliku PDF do klienta
    res.download(filePath, fileName, (err) => {
      if (err) {
        console.error('Błąd przy pobieraniu pliku:', err);
      }
      // Po pobraniu pliku, usuwamy go z serwera
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
