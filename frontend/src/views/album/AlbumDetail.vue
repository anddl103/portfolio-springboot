<template>
  <v-container
    v-if="dataFetched"
    class="pa-4"
  >
    <common-error-alert
      id="error"
      :error="error"
      :error-msg="errorMsg"
      @close="resetError"
    />

    <common-confirm-dialog
      :dialog-handler.sync="confirmDialog"
      :dialog-content="confirmDialogContent"
      @confirm="confirmSubmit"
    />

    <album-form
      :album.sync="album"
      :is-working-album="true"
      @syncArtist="syncMembers"
      @error="setError"
      @resetError="resetError"
    />
    <v-divider
      v-if="album.id"
      inset
      class="my-4 mx-auto"
    />
    <album-card-section
      v-if="albumReady"
      :members.sync="members"
      :artist.sync="artist"
      :album.sync="album"
    />
    <v-divider
      v-if="album.id"
      inset
      class="my-4 mx-auto"
    />
    <album-state-history-section v-if="album.id" />
  </v-container>
</template>
<script>
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import CommonConfirmDialog from '@/components/common/CommonConfirmDialog'
  import commonConfirmMixin from '@/mixins/commonConfirmMixin'
  import AlbumForm from '@/components/album/AlbumForm'
  import AlbumCardSection from '@/components/album/AlbumCardSection'
  import AlbumStateHistorySection from '@/components/album/AlbumStateHistorySection'
  import store from '@/store'
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'
  import ApiList from '@/axios/api-list'
  import axios from '@/axios/index'

  export default {
    name: 'AlbumDetail',
    components: {
      CommonErrorAlert,
      CommonConfirmDialog,
      AlbumForm,
      AlbumCardSection,
      AlbumStateHistorySection,
    },
    mixins: [
      commonErrorMixin,
      commonConfirmMixin,
      calcOffsetMixin,
    ],
    beforeRouteLeave (to, from, next) {
      store.commit('resetTargetItem')
      store.commit('setTargetAlbumId', null)
      next()
    },
    async beforeRouteEnter (to, from, next) {
      async function getContentsTypes () {
        await axios.get(ApiList.contentsTypes)
          .then(res => { store.commit('setContentsTypes', res.data.data) })
          .catch(() => next(from.path))
      }

      async function getWorkingAlbumDetail () {
        await axios.get(ApiList.workingAlbumWithId(store.state.common.targetAlbumId))
          .then(res => { store.commit('setTargetItem', res.data.data) })
          .catch(() => next(from.path))
      }

      async function getAlbumStateHistory () {
        const params = {
          offset: 0,
          limit: -1,
        }
        await axios.get(ApiList.albumStateHistory(store.state.common.targetAlbumId), { params })
          .then(async res => {
            const sortedResult = res.data.data.sort((a, b) => new Date(a.updatedAt) - new Date(b.updatedAt))
            for await (const [idx, hd] of sortedResult.entries()) {
              hd.number = idx + 1
            }
            store.commit('setAlbumStateHistory', sortedResult)
          }).catch(() => next(from.path))
      }

      await getContentsTypes()
      await getWorkingAlbumDetail()
      await getAlbumStateHistory()
      next()
    },
    data: () => ({
      album: {},
      members: [],
      artist: {},
      dataFetched: false,
    }),
    computed: {
      albumReady () {
        return this.album.id !== undefined
      },
    },
    watch: {
      async album () {
        await this.setAlbumStateHistory(this.album.id)
        if (this.members.length <= 0) await this.getMembers(this.album.artistId)
      },
    },
    created () {
      this.init().then(() => {
        this.dataFetched = true
      })
    },
    methods: {
      async init () {
        this.setAlbum()
        await this.getMembers()
      },
      async getMembers (artistId) {
        if (store.state.common.targetIndex === -1) return
        if (artistId === undefined) artistId = store.state.common.targetItem.artistId
        this.artist = await axios.get(ApiList.artistWithId(artistId)).then(res => { return res.data.data })
        this.members = this.artist.members
      },
      setAlbum () {
        if (store.state.common.targetIndex === -1) {
          // create
          this.album = {
            title: undefined,
            description: undefined,
            reward: {},
          }
        } else {
          this.album = store.state.common.targetItem
        }
      },
      syncMembers (artistId) {
        this.getMembers(artistId)
      },
      async setAlbumStateHistory (albumId) {
        const params = this.getParamsForAll()
        await axios.get(ApiList.albumStateHistory(albumId), { params }).then(async res => {
          res.data.data.sort((a, b) => new Date(a.updatedAt) - new Date(b.updatedAt))
          for await (const [idx, hd] of res.data.data.entries()) {
            hd.number = idx + 1
          }
          await store.commit('setAlbumStateHistory', res.data.data)
        })
      },
    },
  }
</script>
