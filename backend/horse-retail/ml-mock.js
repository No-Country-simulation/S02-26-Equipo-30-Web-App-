const http = require('http');
const server = http.createServer((req, res) => {
  if (req.method === 'POST' && req.url === '/predict/trust-score') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ trustScore: 0.87, modelVersion: 'mock-v1', generatedAt: new Date().toISOString() }));
    return;
  }
  if (req.url === '/health') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ status: 'ok' }));
    return;
  }
  res.writeHead(404, { 'Content-Type': 'application/json' });
  res.end(JSON.stringify({ error: 'not found' }));
});
server.listen(8001, '0.0.0.0', () => console.log('ml-mock listening on 8001'));
