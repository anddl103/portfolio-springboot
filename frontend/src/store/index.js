import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate'
import auth from './modules/auth'
import common from './modules/common'

const { instance } = require('@/secure-ls')

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    auth: auth,
    common: common,
  },
  plugins: [
    createPersistedState(
      {
      storage: {
        getItem: key => instance.get(key),
        setItem: (key, value) => instance.set(key, value),
        removeItem: key => instance.remove(key),
      },
    },
    ),
  ],
})

export default store
