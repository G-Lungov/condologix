import { createWorker } from 'tesseract.js';

document.getElementById('saveButton').addEventListener('click', saveImage);

// Function to save the image at sessionStorage
function saveImage() {
  const fileInput = document.getElementById('imageInput');
  const file = fileInput.files[0];

  if (file) {
      const reader = new FileReader();
      reader.onload = function(event) {
          const base64String = event.target.result;
          sessionStorage.setItem('latestImage', base64String);

          // After save it, proccess it
          processImage();
      };
      reader.readAsDataURL(file);
  } else {
      alert('Por favor, selecione uma imagem primeiro.');
  }
}

// Script to read the image
(async function processImage() {
  const imageData = sessionStorage.getItem('latestImage');
  const worker = await createWorker('por');
  const ret = await worker.recognize(imageData);
  const textExtracted = ret.data.text;
  console.log(textExtracted);
  await worker.terminate();

    // Function to extract name, block and apartment from the recognized text/image
    const extractedInfo = extractInfoFromText(text);
    if (extractedInfo) {
    // Script to fill the blanks after recognized
    document.getElementById('name').value = extractedInfo.name || '';
    document.getElementById('terminal').value = extractedInfo.apartment || '';
    document.getElementById('block').value = extractedInfo.block || '';
  }
 })();
 
// Function to extract the data
function extractInfoFromText(text) {
  let name = '';
  let apartment = '';
  let block = '';

  // Divide the text by lines
  const lines = text.split('\n');
  
  // Control variables to identify if name and address were already obtained
  let nameCaptured = false;
  let addressCaptured = false;

  lines.forEach((line, index) => {
    line = line.trim();

    // Extract name (based on it being the next line after destiny)
    if (!nameCaptured && (line.toLowerCase().includes('destinatário') || line.match(/[a-zA-Z]+\s[a-zA-Z]+/))) {
      name = line.split('destinatário')[1]?.trim() || line.trim();
      nameCaptured = true;
    }

    // After name, normaly the next line is the address
    if (nameCaptured && !addressCaptured) {
      if (line.match(/\d{1,5}[\s,\-]*[a-zA-Z]/)) { // Identify line with address
        addressCaptured = true;
      }
    }

    // Extract block and temrinal/apartment on the next lines
    if (addressCaptured) {
      // Search for "block" word if it's present
      if (!block && line.toLowerCase().includes('bloco')) {
        const blocoMatch = line.match(/bloco\s*([A-Z])/i);
        if (blocoMatch) {
          block = blocoMatch[1];
        }
      }
      // Search for "apto" word if it's present
      if (!apartment && line.toLowerCase().includes('apto')) {
        const aptoMatch = line.match(/apto\s*(\d+)/i);
        if (aptoMatch) {
          apartment = aptoMatch[1];
        }
      }

      // If not found "bloco" or "apto", try to catch patterns
      if (!block && !apartment) {
        // Try to catch patterns such as "Bloco A, apto 63"
        const combinedMatch = line.match(/bloco\s*([A-Z])\s*,\s*apto\s*(\d+)/i);
        if (combinedMatch) {
          block = combinedMatch[1];
          apartment = combinedMatch[2];
        } else {
          // If the line contains only numbers, probably it's the apartment
          const genericAptoMatch = line.match(/(\d+)/);
          if (genericAptoMatch) {
            apartment = genericAptoMatch[1];
          }
        }
      }
    }
  });

  return { name, block, apartment };
}