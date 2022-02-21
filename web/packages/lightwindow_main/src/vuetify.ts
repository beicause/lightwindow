import Vue from 'vue'
import Vuetify from 'vuetify/lib/framework'
import { Resize } from 'vuetify/lib'
import '@mdi/font/css/materialdesignicons.min.css' // Ensure you are using css-loader

Vue.use(Vuetify)
Vue.directive('resize', Resize)
export default new Vuetify({
  breakpoint: {
    mobileBreakpoint: 'xs'
  }
})
