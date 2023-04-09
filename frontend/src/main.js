import Vue from 'vue'
import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify'
import './plugins'
import store from './store'
import Toasted from 'vue-toasted'
import vueMoment from 'vue-moment'
import {
  ValidationProvider,
  extend,
  ValidationObserver,
  localize, // 지역
} from 'vee-validate'
import * as rules from 'vee-validate/dist/rules'
import ko from 'vee-validate/dist/locale/ko.json'
import { firebaseAuth } from '@/firebase'
import moment from 'moment-timezone'

Vue.config.productionTip = false
Vue.use(Toasted)
Vue.use(vueMoment)
console.log(`현재 환경은 '${process.env.NODE_ENV}' 입니다.`)
console.log(`현재 VUE_APP_STATE 환경은 '${process.env.VUE_APP_STATE}' 입니다.`)
console.log(`현재 VUE_APP_BASEURL 환경은 '${process.env.VUE_APP_BASE_URL}' 입니다.`)

Vue.prototype.moment = moment
window.$toasted = Vue.prototype.$toasted
// window.$moment = Vue.prototype.$moment

extend('mimes', {
  ...rules.mimes,
  message: '사용 가능한 확장자가 아닙니다.',
})
extend('required', {
  ...rules.required,
  message: '필수 항목입니다.',
})
localize({ ko })
localize('ko')
Vue.component('ValidationProvider', ValidationProvider)
Vue.component('ValidationObserver', ValidationObserver)

let app

firebaseAuth.onAuthStateChanged(function (user) {
  if (!app) {
    app = new Vue({
      render: h => h(App),
      router,
      store,
      vuetify,
    }).$mount('#app')
  }
})
