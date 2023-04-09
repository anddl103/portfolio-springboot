const tagFormatMixin = {
  methods: {
    formatTags (tags) {
      if (tags === undefined || tags === null) return
      let res = ''
      if (Object.keys(tags).length > 0) {
        res = tags.join(', #')
      }
      const prefix = '#'
      return prefix.concat(res)
    },
  },
}

export default tagFormatMixin
