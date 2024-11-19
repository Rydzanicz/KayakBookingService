const express = require('express'); // Importowanie Express.js
const PDFDocument = require('pdfkit'); // Importowanie pdfkit
const fs = require('fs'); // Wbudowany moduł do pracy z plikami

const app = express();
const port = 3000;

// Middleware do obsługi JSON
app.use(express.json());

// Endpoint REST do generowania pliku PDF
app.post('/generate-pdf', (req, res) => {
    const { title, content } = req.body;

    if (!title || !content) {
        return res.status(400).send({ error: 'Title and content are required.' });
    }

    // Tworzenie nowego dokumentu PDF
    const doc = new PDFDocument();
    const filePath = `output-${Date.now()}.pdf`;
    const writeStream = fs.createWriteStream(filePath);

    // Zapisanie strumienia do pliku
    doc.pipe(writeStream);

    // Dodanie zawartości do PDF
    doc.fontSize(20).text(title, { align: 'center' });
    doc.moveDown();
    doc.fontSize(14).text(content, { align: 'left' });

    // Zakończenie tworzenia PDF
    doc.end();

    // Po zapisaniu wysyłamy plik jako odpowiedź
    writeStream.on('finish', () => {
        res.download(filePath, filePath, (err) => {
            if (err) {
                console.error(err);
                res.status(500).send('Could not download the file.');
            } else {
                // Usunięcie pliku po wysłaniu
                fs.unlinkSync(filePath);
            }
        });
    });
});

// Uruchomienie serwera
app.listen(port, () => {
    console.log(`PDF Generator API is running on http://localhost:${port}`);
});
