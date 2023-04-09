module.exports = {
  outputDir: '../backend/src/main/resources/static',
  indexPath: '../static/index.html',
  devServer: {
    proxy: 'http://localhost:8080',
  },
  transpileDependencies: ['vuetify'],

}
