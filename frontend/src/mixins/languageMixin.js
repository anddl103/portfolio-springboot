import axios from '@/axios/index'
import ApiList from '@/axios/api-list'

const languageMixin = {
  data () {
    return {
      languages: [],
    }
  },
  methods: {
    formatData (rawData) {
      return {
        id: rawData.id,
        searchTag: rawData.searchTag,
        ko: rawData.values.ko,
        en: rawData.values.en,
        ja: rawData.values.ja,
      }
    },
    async getLanguages () {
      axios.get(ApiList.languagePacks).then(res => {
        if (!this.validateResponse(res)) return
        const arr = []
        for (const data of res.data.data) {
          const record = this.formatData(data)
          arr.push(record)
        }
        this.languages = arr
      }).catch(err => {
        this.setError(err)
      })
    },
  },
}

export default languageMixin
