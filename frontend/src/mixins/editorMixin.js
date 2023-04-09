import store from '@/store'

const editorMixin = {
  data: () => ({
    localeCodes: store.state.common.localeCodes,
    editorHasError: false,
    resetEditComponent: false,
    resetArr: [],
  }),
  methods: {
    syncResetState (bool) {
      this.resetArr.push(bool)
      if (this.resetArr.length === this.localeCodes.length) {
        this.resetEditComponent = false
        this.resetArr = []
      }
    },
    resetTarget () {
      this.targetItem = JSON.parse(JSON.stringify(this.defaultItem))
      this.resetEditComponent = true
    },
    checkNoContents (val) {
      const regex = new RegExp('<[^>]*>', 'g')
      return val.replaceAll(regex, '').trim().length === 0
    },
    checkEditForm (target) {
      const con = target === undefined
      if (!con) {
        if (this.checkNoContents(target)) {
          this.handleEditorError(true)
          return false
        }
      }
      this.handleEditorError(con)
      return !con
    },
    handleEditorError (val) {
      this.editorHasError = val
    },
  },
}

export default editorMixin
