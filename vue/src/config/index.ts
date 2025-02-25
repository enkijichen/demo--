const config = {
  development: {
    apiBaseUrl: 'http://localhost:8080/api',
    timeout: 5000
  },
  production: {
    apiBaseUrl: '/api',
    timeout: 10000
  }
}

export default config[import.meta.env.MODE || 'development']