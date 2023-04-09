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

    <album-deployed-album-section
      :album="album"
      :artist="artist"
      :is-working-album="false"
      @syncArtist="syncMembers"
      @error="setError"
      @resetError="resetError"
    />
    <v-divider
      v-if="album.id"
      inset
      class="my-4 mx-auto"
    />
    <album-deployed-card-section
      v-if="album.id"
      :members="members"
      :artist="artist"
      :album="album"
    />
    <v-divider
      v-if="album.id"
      inset
      class="my-4 mx-auto"
    />
    <v-row
      class="ma-auto width-webkit-fill"
      justify="center"
    >
      <v-btn
        class="ma-auto"
        to="/album-deployed"
      >
        {{ '뒤로' }}
      </v-btn>
    </v-row>
  </v-container>
</template>
<script>
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import commonConfirmMixin from '@/mixins/commonConfirmMixin'
  import AlbumDeployedAlbumSection from '@/components/album-deployed/AlbumDeployedAlbumSection'
  import AlbumDeployedCardSection from '@/components/album-deployed/AlbumDeployedCardSection'
  import store from '@/store'
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'
  import ApiList from '@/axios/api-list'
  import axios from '@/axios/index'

  export default {
    name: 'AlbumDeployedDetail',
    components: {
      CommonErrorAlert,
      AlbumDeployedAlbumSection,
      AlbumDeployedCardSection,
    },
    mixins: [
      commonErrorMixin,
      commonConfirmMixin,
      calcOffsetMixin,
    ],
    beforeRouteLeave (to, from, next) {
      store.commit('resetTargetItem')
      next()
    },
    async beforeRouteEnter (to, from, next) {
      if (store.state.common.targetIndex > -1) {
        const targetItem = await axios.get(ApiList.albumWithId(store.state.common.targetAlbumId))
        await store.commit('setTargetItem', targetItem.data.data)
        next()
      } else {
        next()
      }
    },
    data: () => ({
      album: store.state.common.targetItem,
      members: [],
      artist: {},
      dataFetched: false,
    }),
    created () {
      this.init().then(() => {
        this.dataFetched = true
      })
    },
    methods: {
      async init () {
        await this.getMembers()
      },
      async getMembers (artistId) {
        if (artistId === undefined) artistId = store.state.common.targetItem.artistId
        this.artist = await axios.get(ApiList.artistWithId(artistId)).then(res => { return res.data.data })
        this.members = this.artist.members
      },
      syncMembers (artistId) {
        this.getMembers(artistId)
      },
    },
  }
</script>
