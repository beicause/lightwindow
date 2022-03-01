import Vue from 'vue'
import App from './App.vue'
import router from './router'
import vuetify from './vuetify'
import CompositionApi from '@vue/composition-api'
import './common/css/fontawesome/css/all.min.css'
import 'virtual:windi.css'

Vue.config.productionTip = false
Vue.use(CompositionApi)
new Vue({
  router,
  vuetify,
  render: h => h(App)
}).$mount('#app')
