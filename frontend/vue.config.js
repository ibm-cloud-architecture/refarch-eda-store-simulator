module.exports = {
  devServer: {
    proxy: {
      '^/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true
      },
    },
    port: 4545
  },
  outputDir: 'target/dist',
  assetsDir: 'static',
  "transpileDependencies": [
    "vuetify"
  ]
}