// client.js (proxy + static server)
// Replaces the original mock logic and proxies requests to the Java server via AOK_PROTOCOL.
// Also serves static files from ./src and exposes a minimal SSE stream for realtime game messages.

const net = require('net');
const fs = require('fs');
const path = require('path');
const url = require('url');
const crypto = require('crypto');
const { request, FullDuplexSession } = require('./oakClient');

// ==== Config ====
const PORT_CLIENT = 3000; 
const JAVA_HOST = 'localhost';
const JAVA_PORT = 8080;

// Map token -> FullDuplexSession
const sessions = new Map();

// Simple helpers
function sendResponse(socket, statusCode, contentType, data) {
  socket.write(`HTTP/1.1 ${statusCode}\r\n`);
  socket.write(`Content-Type: ${contentType}; charset=utf-8\r\n`);
  socket.write('Cache-Control: no-cache\r\n');
  socket.write('Access-Control-Allow-Origin: *\r\n');
  socket.write('Access-Control-Allow-Headers: Content-Type, Authorization\r\n');
  socket.write('Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS\r\n');
  socket.write('\r\n');
  socket.write(data);
  socket.end();
}

function sendJson(socket, statusCode, obj) {
  sendResponse(socket, statusCode, 'application/json', JSON.stringify(obj));
}

function serveStaticFile(socket, pathName) {
  let filePath = './src/index.html';
  if (pathName !== '/') {
    // Keep compatibility with existing structure
    if (pathName.startsWith('/scripts/')) {
      filePath = './src' + pathName;
    } else if (pathName.startsWith('/assets/')) {
      filePath = './src' + pathName;
    } else if (pathName.startsWith('/styles/')) {
      filePath = './src' + pathName;
    } else {
      filePath = '.' + decodeURIComponent(pathName);
    }
  }

  fs.access(filePath, fs.constants.F_OK, (err) => {
    if (err) {
      sendResponse(socket, 404, 'text/html', `<h1>404</h1><p>${filePath} not found</p>`);
      return;
    }
    const ext = path.extname(filePath).toLowerCase();
    const ct = ext === '.js' ? 'application/javascript'
      : ext === '.css' ? 'text/css'
      : ext === '.html' ? 'text/html'
      : ext === '.png' ? 'image/png'
      : ext === '.jpg' || ext === '.jpeg' ? 'image/jpeg'
      : ext === '.gif' ? 'image/gif'
      : ext === '.svg' ? 'image/svg+xml'
      : 'application/octet-stream';
    fs.readFile(filePath, (err, data) => {
      if (err) return sendResponse(socket, 500, 'text/plain', 'Internal error');
      sendResponse(socket, 200, ct, data);
    });
  });
}

function readJsonBody(rawRequest) {
  try {
    const parts = rawRequest.split('\r\n\r\n');
    const body = parts[1] || '';
    return JSON.parse(body || '{}');
  } catch {
    return {};
  }
}

function getAuthToken(headers) {
  const auth = headers.find(h => h.toLowerCase().startsWith('authorization:'));
  if (!auth) return null;
  const value = auth.split(':')[1].trim();
  if (value.toLowerCase().startsWith('bearer ')) {
    return value.slice(7).trim();
  }
  return value;
}

function parseHeaders(headerLines) {
  const headers = [];
  for (let i = 1; i < headerLines.length; i++) {
    const line = headerLines[i];
    if (!line) continue;
    headers.push(line);
  }
  return headers;
}

// SSE support
function startSSE(socket) {
  // Note: net raw socket is used to speak HTTP response for SSE
  socket.write('HTTP/1.1 200 OK\r\n');
  socket.write('Content-Type: text/event-stream\r\n');
  socket.write('Cache-Control: no-cache\r\n');
  socket.write('Connection: keep-alive\r\n');
  socket.write('Access-Control-Allow-Origin: *\r\n');
  socket.write('\r\n');
  return {
    send: (event, data) => {
      if (event) socket.write(`event: ${event}\n`);
      socket.write(`data: ${JSON.stringify(data)}\n\n`);
    },
    end: () => socket.end()
  };
}

// Routes proxying to AOK server
async function handleApi(socket, method, pathName, headers, rawReq) {
  // CORS preflight
  if (method === 'OPTIONS') {
    sendResponse(socket, 204, 'text/plain', '');
    return;
  }

  const token = getAuthToken(headers);
  const body = readJsonBody(rawReq);

  try {
    // Auth
    if (pathName === '/auth/login' && method === 'POST') {
      const resp = await request({
        host: JAVA_HOST, port: JAVA_PORT, method: 'POST', route: '/auth/login',
        data: { ...body }
      });
      return sendJson(socket, 200, resp);
    }
    if (pathName === '/auth/register' && method === 'POST') {
      const resp = await request({
        host: JAVA_HOST, port: JAVA_PORT, method: 'POST', route: '/auth/register',
        data: { ...body }
      });
      return sendJson(socket, 201, resp);
    }

    // Cards / Deck / Packs / Matches — straight proxy with token
    if (pathName === '/card' && method === 'GET') {
      const resp = await request({
        host: JAVA_HOST, port: JAVA_PORT, method: 'GET', route: '/card',
        data: { token }
      });
      return sendJson(socket, 200, resp);
    }

    if (pathName === '/card/open' && method === 'GET') {
      const resp = await request({
        host: JAVA_HOST, port: JAVA_PORT, method: 'GET', route: '/card/open',
        data: { token }
      });
      return sendJson(socket, 200, resp);
    }

    if (pathName === '/deck' && method === 'GET') {
      const resp = await request({
        host: JAVA_HOST, port: JAVA_PORT, method: 'GET', route: '/deck',
        data: { token }
      });
      return sendJson(socket, 200, resp);
    }

    if (pathName === '/deck' && method === 'PUT') {
      const resp = await request({
        host: JAVA_HOST, port: JAVA_PORT, method: 'PUT', route: '/deck',
        data: { token, ...body }
      });
      return sendJson(socket, 200, resp);
    }

    if (pathName === '/match' && method === 'GET') {
      const resp = await request({
        host: JAVA_HOST, port: JAVA_PORT, method: 'GET', route: '/match',
        data: { token }
      });
      return sendJson(socket, 200, resp);
    }

    // === Real-time game via SSE + FullDuplexSession ===
    if (pathName === '/game/stream' && method === 'GET') {
      if (!token) return sendJson(socket, 401, { error: 'Missing token' });
      // Start SSE
      const sse = startSSE(socket);
      // Reuse or create FullDuplex session
      let sess = sessions.get(token);
      if (!sess) {
        sess = new FullDuplexSession({
          host: JAVA_HOST,
          port: JAVA_PORT,
          auth: { token }
        });
        sessions.set(token, sess);
        // Auto-clean on close
        sess.on('close', () => sessions.delete(token));
        sess.on('error', (e) => {
          try { sse.send('error', { message: e.message }); } catch {}
        });
        sess.on('message', (msg) => {
          // Relay any body/type to SSE
          if (msg && msg.body) {
            const ev = msg.body.type || 'message';
            sse.send(ev, msg.body);
          } else {
            sse.send('message', { raw: msg.raw || '' });
          }
        });
        // Connect and send initial joinQueue by default (can be customized via /game/send)
        sess.connect({ type: 'joinQueue' }).catch(err => {
          try { sse.send('error', { message: err.message }); } catch {}
        });
      } else {
        // If already connected, just attach — new client will receive stream
      }
      // Keep alive comments
      const keepAlive = setInterval(() => {
        try { socket.write(': keep-alive\n\n'); } catch {}
      }, 15000);

      socket.on('close', () => {
        clearInterval(keepAlive);
        // Do not close the session here — allow multiple clients
      });

      return;
    }

    if (pathName === '/game/send' && method === 'POST') {
      if (!token) return sendJson(socket, 401, { error: 'Missing token' });
      let sess = sessions.get(token);
      if (!sess) {
        sess = new FullDuplexSession({
          host: JAVA_HOST,
          port: JAVA_PORT,
          auth: { token }
        });
        sessions.set(token, sess);
        await sess.connect(); // connect without sending join
      }
      // Forward arbitrary game command
      sess.send(body, { method: 'POST', route: '/game' });
      return sendJson(socket, 200, { ok: true });
    }

    if (pathName === '/game/close' && method === 'POST') {
      if (!token) return sendJson(socket, 401, { error: 'Missing token' });
      const sess = sessions.get(token);
      if (sess) {
        try { sess.close(); } catch {}
        sessions.delete(token);
      }
      return sendJson(socket, 200, { ok: true });
    }

    // Fallback — static
    return serveStaticFile(socket, pathName);
  } catch (err) {
    console.error('Proxy error:', err);
    return sendJson(socket, 500, { error: err.message || 'Internal error' });
  }
}

// TCP HTTP server
const server = net.createServer((socket) => {
  let req = '';
  socket.on('data', (chunk) => {
    req += chunk.toString('utf8');
    if (req.includes('\r\n\r\n')) {
      const lines = req.split('\r\n');
      const [method, pathName] = lines[0].split(' ');
      const headers = parseHeaders(lines);
      // API paths we handle
      const apiPaths = [
        '/auth/login','/auth/register','/card','/card/open','/deck','/match',
        '/game/stream','/game/send','/game/close'
      ];
      if (apiPaths.includes(pathName)) {
        handleApi(socket, method, pathName, headers, req);
      } else {
        serveStaticFile(socket, pathName);
      }
    }
  });
  socket.on('error', () => {});
});

server.listen(PORT_CLIENT, () => {
  console.log(`[client-proxy] Listening on http://localhost:${PORT_CLIENT}`);
  console.log(`[client-proxy] Proxying to AOK ${JAVA_HOST}:${JAVA_PORT}`);
});