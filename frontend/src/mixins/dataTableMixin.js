const dataTableMixin = {
  data () {
    return {
      targetFilter: '',
      tableHeader: [],
      tableData: [],
      dialog: false,
      dialogDelete: false,
      loading: false,
    }
  },
  methods: {
    watchOptions (options) {
      this.options = options
    },
    watchKeyword (keyword) {
      this.keyword = keyword
      this.getTableData()
    },
    watchFilter (filter) {
      this.targetFilter = filter
    },
    editItem (item) {
      this.setTarget(item)
      const idx = this.tableData.indexOf(item)
      this.$store.commit('setTargetItem', item)
      this.$store.commit('setTargetIndex', idx)
      this.dialog = true
    },
    deleteItem () {
      this.getTableData()
    },
    closeDelete () {
      this.setDefault()
      this.dialogDelete = false
    },
    async deleteItemConfirm () {
      this.closeDelete()
      await this.getTableData()
    },
    close () {
      this.setDefault()
      this.dialog = false
    },
    setDefault () {
      this.editedItem = Object.assign({}, this.defaultItem)
      this.editedIndex = -1
    },
    setTarget (item) {
      this.editedIndex = this.tableData.indexOf(item)
      this.editedItem = Object.assign({}, item)
    },
    save () {
      this.getTableData()
      this.close()
    },
    update () {
      this.getTableData()
      this.close()
    },
    loadingStart () {
      this.loading = true
    },
    loadingDone () {
      this.loading = false
    },
  },
}

export default dataTableMixin
