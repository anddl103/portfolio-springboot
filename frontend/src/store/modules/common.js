const getDefaultItem = () => {
  return {
    targetItem: {},
    targetIndex: -1,
  }
}

const getDefaultAlbumCard = () => {
  return {
    targetAlbumCard: {
      albumId: '',
      artistId: '',
      id: '',
      members: [],
      operationType: '',
      position: -1,
    },
    targetAlbumCardIndex: -1,
  }
}

const getDefaultState = () => {
  const { targetItem, targetIndex } = getDefaultItem()
  const { targetAlbumCard, targetAlbumCardIndex } = getDefaultAlbumCard()
  return {
    selectedMenu: '',
    loading: false,
    loadingValue: 0,
    artists: [],
    albums: [],
    cards: [],
    pageInfo: null,
    targetItem: targetItem,
    targetIndex: targetIndex,
    targetAlbumCard: targetAlbumCard,
    targetAlbumCardIndex: targetAlbumCardIndex,
    faqCategories: [],
    localeCodes: [],
    sideNavi: true,
    albumStateHistory: [],
    targetAlbumId: null,
    batchState: null,
    contentsTypes: [],
  }
}
// initial state
const state = getDefaultState()

const mutations = {
  setSelectedMenu (state, menuName) {
    state.selectedMenu = menuName
  },
  setLoading (state, flag) {
    state.loading = flag
  },
  setLoadingValue (state, num) {
    state.loadingValue = num
  },
  resetLoadingValue (state) {
    state.loadingValue = 0
  },
  setArtists (state, arr) {
    state.artists = arr
  },
  setAlbums (state, arr) {
    state.albums = arr
  },
  setTargetItem (state, item) {
    if (item === undefined) {
      state.targetItem = getDefaultItem().targetItem
    } else {
      state.targetItem = item
    }
  },
  setTargetIndex (state, idx) {
    state.targetIndex = idx
  },
  resetTargetItem (state) {
    state.targetItem = getDefaultItem().targetItem
    state.targetIndex = getDefaultItem().targetIndex
  },
  setTargetAlbumCard (state, item) {
    if (item === undefined) {
      state.targetAlbumCard = getDefaultItem().targetAlbumCard
    } else {
      state.targetAlbumCard = item
    }
  },
  setTargetAlbumCardIndex (state, idx) {
    state.targetAlbumCardIndex = idx
  },
  resetTargetAlbumCard (state) {
    state.targetAlbumCard = getDefaultAlbumCard().targetAlbumCard
    state.targetAlbumCardIndex = getDefaultAlbumCard().targetAlbumCardIndex
  },
  setFaqCategories (state, item) {
    state.faqCategories = item
  },
  setLocaleCodes (state, arr) {
    state.localeCodes = arr
  },
  setSideNavi (state, bool) {
    state.sideNavi = bool
  },
  setAlbumStateHistory (state, arr) {
    state.albumStateHistory = arr
  },
  setTargetAlbumId (state, id) {
    state.targetAlbumId = id
  },
  setBatchState (state, bool) {
    state.batchState = bool
  },
  setContentsTypes (state, arr) {
    state.contentsTypes = arr
  },
}

const actions = {
  async logout () {
    console.log('------------- logout action common -------------')
    Object.assign(state, getDefaultState())
  },
  async setLoadingTrue ({ commit }) {
    commit('resetLoadingValue')
    commit('setLoading', true)
  },
  async setLoadingFalse ({ commit }) {
    commit('setLoading', false)
    commit('resetLoadingValue')
  },
  async setTarget ({ commit }, data) {
    const { target, idx } = data
    commit('setTargetItem', target)
    commit('setTargetIndex', idx)
  },
}

const Common = {
  state,
  mutations,
  actions,
}
export default Common
