const express = require('express');
const fs = require('fs');
const path = require('path');
const PDFDocument = require('pdfkit');
const app = express();

// Ustawienie EJS jako silnik szablonów
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

// Middleware do parsowania formularzy
app.use(express.urlencoded({ extended: true }));

// Strona z formularzem
app.get('/', (req, res) => {
  res.render('form');
});

// Endpoint generujący PDF
app.post('/generate-pdf', (req, res) => {
  const { sellerName, sellerAddress, sellerNip, sellerBankAccount, buyerName, buyerAddress, buyerNip,
    invoiceDate, saleDate, invoiceNumber, description, quantity, unitPrice, vatRate } = req.body;

  const doc = new PDFDocument();
  const filePath = path.join(__dirname, `${invoiceNumber}.pdf`);
  doc.pipe(fs.createWriteStream(filePath));

  // Dodajemy nagłówek faktury
  doc.fontSize(20).text('Faktura VAT', { align: 'center' });
  doc.moveDown();

  // Dane sprzedawcy
  doc.fontSize(12).text(`Sprzedawca: ${sellerName}`);
  doc.text(`Adres: ${sellerAddress}`);
  doc.text(`NIP: ${sellerNip}`);
  doc.text(`Nr konta: ${sellerBankAccount}`);
  doc.moveDown();

  // Dane nabywcy
  doc.text(`Nabywca: ${buyerName}`);
  doc.text(`Adres: ${buyerAddress}`);
  doc.text(`NIP: ${buyerNip}`);
  doc.moveDown();

  // Szczegóły faktury
  doc.text(`Data wystawienia: ${invoiceDate}`);
  doc.text(`Data sprzedaży: ${saleDate}`);
  doc.text(`Numer faktury: ${invoiceNumber}`);
  doc.moveDown();

  // Szczegóły pozycji faktury
  doc.text('Opis towaru/usługi: ' + description);
  doc.text('Ilość: ' + quantity);
  doc.text('Cena netto: ' + unitPrice);
  doc.text('Stawka VAT: ' + vatRate);
  const netAmount = quantity * unitPrice;
  const vatAmount = netAmount * (vatRate / 100);
  const grossAmount = netAmount + vatAmount;
  doc.text('Wartość netto: ' + netAmount.toFixed(2));
  doc.text('Wartość VAT: ' + vatAmount.toFixed(2));
  doc.text('Wartość brutto: ' + grossAmount.toFixed(2));
  doc.moveDown();

  // Podsumowanie
  doc.text(`Suma netto: ${netAmount.toFixed(2)}`);
  doc.text(`Suma VAT: ${vatAmount.toFixed(2)}`);
  doc.text(`Suma brutto: ${grossAmount.toFixed(2)}`);

  doc.end();

  // Zwrócenie pliku PDF
  res.download(filePath, `${invoiceNumber}.pdf`, (err) => {
    if (err) {
      console.log(err);
    }
    fs.unlinkSync(filePath); // Usuwamy plik po wysłaniu
  });
});

// Uruchomienie serwera
app.listen(3000, () => {
  console.log('Server is running on http://localhost:3000');
});
