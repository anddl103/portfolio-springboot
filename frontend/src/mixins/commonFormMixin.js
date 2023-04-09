import store from '@/store'

const commonFormMixin = {
  beforeRouteLeave (to, from, next) {
    store.commit('resetTargetItem')
    next()
  },
  computed: {
    defaultItem: {
      get () {
        return store.state.common.targetItem
      },
      set (item) {
        this.$store.commit('setTargetItem', item)
      },
    },
    targetIndex: {
      get () {
        return store.state.common.targetIndex
      },
      set (idx) {
        this.$store.commit('setTargetIndex', idx)
      },
    },
    isUpdate () {
      return this.isDetail && this.editMode
    },
  },
}

export default commonFormMixin
