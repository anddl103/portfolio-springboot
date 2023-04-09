const commonConfirmMixin = {
  data () {
    return {
      confirmDialog: false,
      confirmDialogContent: '',
      needAtLeastError: '컨텐츠 이미지와 컨텐츠 비디오 중 하나는 있어야 합니다.',
    }
  },
  methods: {
    confirmSubmit () {
      this.confirmDialog = false
      this.save()
    },
    openConfirmDialog (str) {
      this.confirmDialogContent = str
      this.confirmDialog = true
    },
    checkAtLeastOneContent () {
      const contents = this.uploadedFiles.filter(uf => uf.isKey === true)
      const targets = contents.filter(uf => this.checkFileExists(uf))
      return targets.length > 0
    },
    checkFileExists (target) {
      const con1 = target.model !== null
      const con2 = (target.url !== null && target.url !== '')
      return con1 || con2
    },
  },
}

export default commonConfirmMixin
