const PROXY_CONFIG = [
  {
    context: ['/api'],
    target: 'http://localhost:9090/',
    secure: false,
    changeOrigin: true
  }
];

module.exports = PROXY_CONFIG;
