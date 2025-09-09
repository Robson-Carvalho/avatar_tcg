const net = require('net');
const fs = require('fs');
const path = require('path');

const PORT = 3000;

function getFile(filePath) {
  const ext = path.extname(filePath);
  let contentType = 'text/plain';

  if (ext === '.html') contentType = 'text/html';
  else if (ext === '.js') contentType = 'application/javascript';
  else if (ext === '.css') contentType = 'text/css';

  try {
    const data = fs.readFileSync(filePath);
    return { data, contentType };
  } catch (err) {
    return null;
  }
}

// Cria servidor TCP puro
const server = net.createServer((socket) => {
  socket.on('data', (chunk) => {
    const request = chunk.toString();
    const [requestLine] = request.split('\r\n');
    const [, url] = requestLine.split(' ');

    let filePath = '';
    if (url === '/' || url === '/index.html') {
      filePath = path.join(__dirname, 'src', 'index.html');
    } else {
      filePath = path.join(__dirname, 'src', url);
    }

    const file = getFile(filePath);

    if (file) {
      const response = 
        `HTTP/1.1 200 OK\r\n` +
        `Content-Type: ${file.contentType}\r\n` +
        `Content-Length: ${file.data.length}\r\n` +
        `Connection: close\r\n\r\n`;

      socket.write(response);
      socket.write(file.data);
    } else {
      const notFound = '404 - Not Found';
      socket.write(
        `HTTP/1.1 404 Not Found\r\nContent-Length: ${notFound.length}\r\nConnection: close\r\n\r\n${notFound}`
      );
    }

    socket.end();
  });
});

server.listen(PORT, '0.0.0.0', () => {
    console.log(`http://localhost:${PORT}`);
});

