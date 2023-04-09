const calcOffsetMixin = {
  methods: {
    getOffsetParams (itemsPerPage) {
      const offset = this.options.page === 1 ? 0 : ((this.options.page - 1) * itemsPerPage)
      return {
        offset: offset,
        limit: itemsPerPage,
      }
    },
    getParamsForAll () {
      return {
        offset: 0,
        limit: -1,
      }
    },
  },
}

export default calcOffsetMixin
