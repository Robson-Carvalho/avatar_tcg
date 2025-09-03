// client.js - Versão completa corrigida
const net = require('net');
const fs = require('fs');
const path = require('path');
const { request, FullDuplexSession } = require('./oakClient');

const PORT_CLIENT = 3000;
const JAVA_HOST = 'localhost';
const JAVA_PORT = 8080;

const sessions = new Map();

function sendResponse(socket, statusCode, contentType, data) {
  socket.write(`HTTP/1.1 ${statusCode}\r\n`);
  socket.write(`Content-Type: ${contentType}; charset=utf-8\r\n`);
  socket.write('Cache-Control: no-cache\r\n');
  socket.write('Access-Control-Allow-Origin: *\r\n');
  socket.write('Access-Control-Allow-Headers: Content-Type, Authorization\r\n');
  socket.write('Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS\r\n');
  socket.write('Connection: close\r\n');
  socket.write('\r\n');
  socket.write(data);
  socket.end();
}

function sendJson(socket, statusCode, obj) {
  sendResponse(socket, statusCode, 'application/json', JSON.stringify(obj));
}

function serveStaticFile(socket, pathName) {
  let filePath = './src' + (pathName === '/' ? '/index.html' : pathName);
  
  fs.access(filePath, fs.constants.F_OK, (err) => {
    if (err) {
      sendResponse(socket, 404, 'text/html', `<h1>404 - ${filePath} not found</h1>`);
      return;
    }
    
    const ext = path.extname(filePath).toLowerCase();
    const contentTypes = {
      '.js': 'application/javascript',
      '.css': 'text/css',
      '.html': 'text/html',
      '.png': 'image/png',
      '.jpg': 'image/jpeg',
      '.jpeg': 'image/jpeg',
      '.gif': 'image/gif',
      '.svg': 'image/svg+xml'
    };
    
    const contentType = contentTypes[ext] || 'application/octet-stream';
    
    fs.readFile(filePath, (err, data) => {
      if (err) {
        sendResponse(socket, 500, 'text/plain', 'Internal server error');
        return;
      }
      sendResponse(socket, 200, contentType, data);
    });
  });
}

function parseRequest(rawRequest) {
  const lines = rawRequest.split('\r\n');
  const [method, path] = lines[0].split(' ');
  const headers = {};
  
  for (let i = 1; i < lines.length; i++) {
    const line = lines[i];
    if (line === '') break;
    
    const colonIndex = line.indexOf(':');
    if (colonIndex > -1) {
      const key = line.slice(0, colonIndex).trim().toLowerCase();
      const value = line.slice(colonIndex + 1).trim();
      headers[key] = value;
    }
  }
  
  const body = rawRequest.split('\r\n\r\n')[1] || '';
  
  return { method, path, headers, body };
}

function getToken(headers) {
  const authHeader = headers['authorization'];
  if (!authHeader) return null;
  
  if (authHeader.startsWith('Bearer ')) {
    return authHeader.slice(7);
  }
  return authHeader;
}

function startSSE(socket) {
  socket.write('HTTP/1.1 200 OK\r\n');
  socket.write('Content-Type: text/event-stream\r\n');
  socket.write('Cache-Control: no-cache\r\n');
  socket.write('Access-Control-Allow-Origin: *\r\n');
  socket.write('Access-Control-Allow-Headers: Content-Type, Authorization\r\n');
  socket.write('Connection: keep-alive\r\n');
  socket.write('\r\n');
  
  return {
    send: (data) => {
      socket.write(`data: ${JSON.stringify(data)}\n\n`);
    },
    close: () => socket.end()
  };
}

async function handleApiRequest(socket, method, path, headers, body) {
  const token = getToken(headers);
  
  try {
    // CORS preflight
    if (method === 'OPTIONS') {
      sendResponse(socket, 204, 'text/plain', '');
      return;
    }

    // Rota de streaming para realtime
    if (path === '/game/stream' && method === 'GET') {
      if (!token) {
        sendJson(socket, 401, { error: 'Token required' });
        return;
      }

      console.log('Starting SSE connection for token:', token);
      const sse = startSSE(socket);

      let session = sessions.get(token);
      if (!session) {
        session = new FullDuplexSession({
          host: JAVA_HOST,
          port: JAVA_PORT,
          auth: { token }
        });

        sessions.set(token, session);

        session.on('message', (msg) => {
          console.log('Received message from Java:', msg);
          sse.send(msg.body || msg);
        });

        session.on('error', (err) => {
          console.error('Session error:', err);
          sse.send({ error: err.message });
        });

        session.on('close', () => {
          console.log('Session closed for token:', token);
          sessions.delete(token);
          sse.send({ type: 'SESSION_CLOSED' });
        });

        console.log("oi")

        // Connect to Java server
        await session.connect({ type: 'joinQueue' });
      }

      // Keep connection alive
      const keepAlive = setInterval(() => {
        try {
          socket.write(': keepalive\n\n');
        } catch (e) {
          clearInterval(keepAlive);
        }
      }, 30000);

      socket.on('close', () => {
        clearInterval(keepAlive);
        console.log('SSE connection closed');
      });

      return;
    }

    const apiRoutes = {
      '/auth/login': { method: 'POST', route: '/auth/login' },
      '/auth/register': { method: 'POST', route: '/auth/register' },
      '/card': { method: 'GET', route: '/card' },
      '/card/open': { method: 'GET', route: '/card/open' },
      '/deck': { method: 'GET', route: '/deck' },
      '/deck/update': { method: 'PUT', route: '/deck' },
      '/match': { method: 'GET', route: '/match' },
      '/game/send': { method: 'POST', route: '/game' }
    };

    if (apiRoutes[path] && method === apiRoutes[path].method) {
      const requestData = body ? JSON.parse(body) : {};
      if (token) requestData.token = token;

      const result = await request({
        host: JAVA_HOST,
        port: JAVA_PORT,
        method: apiRoutes[path].method,
        route: apiRoutes[path].route,
        data: requestData
      });

      sendJson(socket, 200, result);
      return;
    }

    // Se não for uma rota API, serve arquivo estático
    serveStaticFile(socket, path);

  } catch (error) {
    console.error('API error:', error);
    sendJson(socket, 500, { error: error.message });
  }
}

// Servidor principal
const server = net.createServer((socket) => {
  let buffer = '';

  socket.on('data', (data) => {
    buffer += data.toString();

    if (buffer.includes('\r\n\r\n')) {
      const request = parseRequest(buffer);
      
      // Verifica se é uma rota API
      const apiPaths = [
        '/auth/login', '/auth/register', '/card', '/card/open', 
        '/deck', '/match', '/game/stream', '/game/send', "/deck/update"
      ];

      if (apiPaths.includes(request.path)) {
        handleApiRequest(socket, request.method, request.path, request.headers, request.body);
      } else {
        serveStaticFile(socket, request.path);
      }
    }
  });

  socket.on('error', (err) => {
    console.error('Socket error:', err);
  });
});

server.listen(PORT_CLIENT, () => {
  console.log(`Proxy server running on http://localhost:${PORT_CLIENT}`);
  console.log(`Proxying to Java server at ${JAVA_HOST}:${JAVA_PORT}`);
});