import store from '@/store'

const commonErrorMixin = {
  data () {
    return {
      error: false,
      errorMsg: '',
    }
  },
  methods: {
    resetError () {
      this.error = false
      this.errorMsg = ''
    },
    setError (err) {
      this.error = true
      this.errorMsg = err
      store.dispatch('setLoadingFalse').then(() => {
        // do nothing
      })
    },
    fileUploadErrorHandler (err) {
      if (err === '') {
        this.error = false
      } else {
        this.setError(err)
      }
    },
    async validateResponse (res) {
      await store.dispatch('setLoadingFalse')
      if (res.data.code !== 0) {
        this.setError(res.data.message)
        return false
      }
      return true
    },
  },
}

export default commonErrorMixin
