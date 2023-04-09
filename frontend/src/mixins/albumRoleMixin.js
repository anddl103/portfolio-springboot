import { albumState, role } from '@/assets/enums'
import store from '@/store'

const albumRoleMixin = {
  computed: {
    isEditor () {
      const editableGroup = [role.CONTENTS_EDITOR]
      return editableGroup.includes(store.state.auth.currentRole)
    },
    isManager () {
      const confirmableGroup = [role.CONTENTS_MANAGER]
      return confirmableGroup.includes(store.state.auth.currentRole)
    },
  },
  methods: {
    isState (state) {
      if (this.album.state === undefined) return false
      const targetState = Object.entries(albumState).find((k, v) => v === this.album.state.code)[1]
      return targetState === state
    },
  },
}

export default albumRoleMixin
