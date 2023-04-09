import store from '@/store'

const albumMultiLanguageTableMixin = {
  computed: {
    languageTitleDescTableHeader () {
      return [
        {
          text: '언어',
          value: 'language',
          align: 'start',
          width: 100,
        },
        {
          text: '앨범 타이틀',
          value: 'albumTitle',
          align: 'start',
        },
        {
          text: '앨범 설명',
          value: 'albumDesc',
          align: 'start',
        },
        {
          text: '리워드 타이틀',
          value: 'rewardTitle',
          align: 'start',
        },
        {
          text: '리워드 설명',
          value: 'rewardDesc',
          align: 'start',
        },
      ]
    },
    tableData () {
      const datas = []
      const localeCodes = store.state.common.localeCodes.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
      for (const lc of localeCodes) {
        const obj = {
          language: lc,
          albumTitle: this.targetItem.title.values[lc.code],
          albumDesc: this.targetItem.description.values[lc.code],
          rewardTitle: this.targetItem.reward.title.values[lc.code],
          rewardDesc: this.targetItem.reward.description.values[lc.code],
        }
        datas.push(obj)
      }
      return datas
    },
    tableDataLanguageData () {
      const arr = []
      function privateFunction (arr, str) {
        if (str !== undefined) {
          arr.push(str)
        }
      }
      for (const td of this.tableData) {
        privateFunction(arr, td.albumTitle)
        privateFunction(arr, td.albumDesc)
        privateFunction(arr, td.rewardTitle)
        privateFunction(arr, td.rewardDesc)
      }
      return arr
    },
  },
}

export default albumMultiLanguageTableMixin
