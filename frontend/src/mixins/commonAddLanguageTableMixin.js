import store from '@/store'

const commonAddLanguageTableMixin = {
  data () {
    return {
      tableHeader: [
        {
          text: '',
          value: 'actions',
          sortable: false,
        },
      ],
      localeCodes: store.state.common.localeCodes,
    }
  },
  mounted () {
    this.setTableHeader()
  },
  methods: {
    setTableHeader () {
      this.localeCodes.sort((b, a) => new Date(a.createdAt) - new Date(b.createdAt))
      for (const lc of this.localeCodes) {
        const obj = {
          text: lc.comment,
          value: lc.code,
          sortable: false,
        }
        this.tableHeader.splice(0, 0, obj)
      }
    },
    addRow (arr) {
      arr.push(this.createInitObj())
    },
    removeRow (arr, idx) {
      arr.splice(idx, 1)
    },
    createInitObj () {
      const val = {}
      for (const lc of this.localeCodes) {
        val[lc.code] = ''
      }
      return {
        id: null,
        name: null,
        values: val,
      }
    },
  },
}

export default commonAddLanguageTableMixin
