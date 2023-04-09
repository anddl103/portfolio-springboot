import { clearLocalStorage } from '@/secure-ls'

const getDefaultState = () => {
  return {
    uid: '',
    roles: [],
    currentRole: '',
  }
}
// initial state
const state = getDefaultState()

const mutations = {
  setUid (state, uid) {
    state.uid = uid
  },
  setRoles (state, arr) {
    state.roles = arr
  },
  setCurrentRole (state, role) {
    state.currentRole = role
  },
}

const actions = {
  async logout () {
    console.log('------------- logout action auth -------------')
    Object.assign(state, getDefaultState())
    clearLocalStorage()
  },
}

const Auth = {
  state,
  mutations,
  actions,
}
export default Auth
