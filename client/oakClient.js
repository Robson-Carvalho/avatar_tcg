// oakClient.js
// Node-side AOK_PROTOCOL client using net.Socket.
// Provides two modes:
// 1) request(method, route, data) -> one-shot half-duplex exchange
// 2) FullDuplexSession -> persistent connection for realtime

const net = require('net');
const EventEmitter = require('events');

function buildRequest(method, route, data) {
  const payload = typeof data === 'string' ? data : JSON.stringify(data || {});
  return `OAK_PROTOCOL\n${method}\n${route}\n${payload}\n\n`;
}

// Na função parseMessage, corrija o parsing do JSON:
function parseMessage(frame) {
  console.log('🔄 Parseando frame:', frame);
  
  const lines = frame.split('\n');
  if (lines.length < 4) {
    throw new Error('Invalid AOK frame (not enough lines)');
  }
  
  const proto = lines[0].trim();
  const method = lines[1].trim();
  const route = lines[2].trim();
  const jsonStr = lines.slice(3).join('\n').trim(); 
  
  console.log('📋 Proto:', proto, 'Method:', method, 'Route:', route);
  console.log('📦 JSON String:', jsonStr);
  
  if (proto !== 'OAK_PROTOCOL') {
    throw new Error('Invalid protocol header');
  }
  
  let body = null;
  try {
    body = jsonStr ? JSON.parse(jsonStr) : null;
    console.log('✅ JSON parseado com sucesso:', body);
  } catch (e) {
    console.log('❌ Erro ao parsear JSON:', e.message);
    body = { raw: jsonStr };
  }
  
  return { method, route, body, raw: frame };
}

function parseMessage(frame) {
  console.log('🔄 Parseando frame:', frame);
  
  const lines = frame.split('\n');
  if (lines.length < 4) {
    throw new Error('Invalid AOK frame (not enough lines)');
  }
  
  const proto = lines[0].trim();
  const method = lines[1].trim();
  const route = lines[2].trim();
  const jsonStr = lines.slice(3).join('\n').trim(); // ⚠️ CORRIGIDO
  
  console.log('📋 Proto:', proto, 'Method:', method, 'Route:', route);
  console.log('📦 JSON String:', jsonStr);
  
  if (proto !== 'OAK_PROTOCOL') {
    throw new Error('Invalid protocol header');
  }
  
  let body = null;
  try {
    body = jsonStr ? JSON.parse(jsonStr) : null;
    console.log('✅ JSON parseado com sucesso:', body);
  } catch (e) {
    console.log('❌ Erro ao parsear JSON:', e.message);
    body = { raw: jsonStr };
  }
  
  return { method, route, body, raw: frame };
}

class FullDuplexSession extends EventEmitter {
  constructor({ host, port, auth }) {
    super();
    this.host = host;
    this.port = port;
    this.auth = auth || {};
    this.socket = null;
    this._bufferRemainder = '';
    this._connected = false;
    this._connecting = false;
    this._heartbeatTimer = null;
  }

  connect(initPayload = {}) {
    return new Promise((resolve, reject) => {
      if (this._connecting || this._connected) {
        return resolve();
      }
      this._connecting = true;

      const socket = new net.Socket();
      this.socket = socket;

      socket.connect(this.port, this.host, () => {
        this._connected = true;
        this._connecting = false;
        // Send the initial fullduplex handshake/message
        const payload = {
          connection: 'fullduplex',
          ...this.auth,
          ...initPayload
        };
        const req = buildRequest('POST', '/game', payload);
        socket.write(req);
        // Start simple keepalive (send ping every 10s)
        this._heartbeatTimer = setInterval(() => {
          try {
            this.send({ type: 'ping' });
          } catch {}
        }, 10000);
        resolve();
      });

      socket.on('data', (chunk) => {
        const { complete, remainder } = parseFrames(this._bufferRemainder + chunk.toString('utf8'));
        this._bufferRemainder = remainder;
        for (const frame of complete) {
          if (!frame.trim()) continue;
          try {
            const msg = parseMessage(frame);
            // Emit raw and type-based
            this.emit('message', msg);
            if (msg?.body?.type) {
              this.emit(msg.body.type, msg.body);
            }
          } catch (err) {
            this.emit('error', err);
          }
        }
      });

      socket.on('error', (err) => {
        this.emit('error', err);
      });

      socket.on('close', () => {
        this._connected = false;
        this._connecting = false;
        if (this._heartbeatTimer) {
          clearInterval(this._heartbeatTimer);
          this._heartbeatTimer = null;
        }
        this.emit('close');
      });
    });
  }

  send(payload, { method = 'REALTIME', route = '/game' } = {}) {
    if (!this.socket || !this._connected) {
      throw new Error('FullDuplex socket not connected');
    }
    const data = {
      ...this.auth,
      ...payload
    };
    const req = buildRequest(method, route, data);
    this.socket.write(req);
  }

  close() {
    if (this._heartbeatTimer) {
      clearInterval(this._heartbeatTimer);
      this._heartbeatTimer = null;
    }
    if (this.socket) {
      try { this.socket.end(); } catch {}
      this.socket = null;
    }
    this._connected = false;
    this._connecting = false;
  }
}

async function request({ host, port, method, route, data }) {
  return new Promise((resolve, reject) => {
    const socket = new net.Socket();
    let buffer = "";

    socket.connect(port, host, () => {
      const payload = { connection: "halfduplex", ...data };
      const req = buildRequest(method, route, payload);
      socket.write(req);
    });

    socket.on("data", (chunk) => {
      buffer += chunk.toString();
    });

    socket.on("end", () => {
      try {
        const lines = buffer.split("\n");
        // ignora primeira linha (OAK_PROTOCOL)
        const jsonStr = lines.slice(1).join("\n").trim();
        const body = JSON.parse(jsonStr);
        resolve(body);
      } catch (err) {
        reject(new Error("Falha ao parsear resposta AOK: " + err.message));
      }
    });

    socket.on("error", (err) => reject(err));
  });
}


module.exports = {
  request,
  FullDuplexSession,
};