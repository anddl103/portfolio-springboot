import axios from 'axios'
import { firebaseAuth } from '@/firebase'
import router from '@/router'
import store from '@/store'

const baseUrl = process.env.VUE_APP_BASE_URL

const http = axios.create({
  baseURL: baseUrl,
  timeout: 300 * 1000,
  headers: {
    'Content-Type': 'application/json',
  },
  preflightContinue: true,
  crossDomain: true,
})

const errorHandler = async (error) => {
  // console.log('error handler: ', error.response)
  if (error.response.status === 401) {
    await store.dispatch('logout')
    await router.push({ name: 'Login' })
  }
  // eslint-disable-next-line prefer-promise-reject-errors
  return Promise.reject({ ...error })
}

http.interceptors.request.use(
  async (config) => {
    if (firebaseAuth.currentUser === null) {
      await router.push({ name: 'Login' })
    } else {
      const token = await firebaseAuth.currentUser.getIdToken()
      config.headers['x-access-token'] = token
    }
    return config
  },
  error => Promise.reject(error),
)

http.interceptors.response.use(
  response => response,
  error => errorHandler(error),
)

export default http
