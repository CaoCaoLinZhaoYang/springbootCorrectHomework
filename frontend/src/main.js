import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'

// 配置axios基础URL
axios.defaults.baseURL = '/api'

Vue.config.productionTip = false
Vue.use(ElementUI)
Vue.prototype.$http = axios

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')