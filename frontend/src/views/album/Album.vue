<template>
  <v-container
    class="pa-4"
    fluid
  >
    <common-error-alert
      :error="error"
      :error-msg="errorMsg"
      @close="resetError"
    />

    <common-table
      :keyword="keyword"
      :table-data="tableData"
      :table-header="tableHeader"
      table-title="앨범"
      :thumbnail="true"
      :detail="true"
      :modify="false"
      :loading="loading"
      :server="true"
      :search="true"
      :total-row="totalRow"
      :delete-url="deleteUrl"
      :allow-create="isEditor"
      :allow-delete="handleAllowDelete()"
      :is-filtering="true"
      :filter-data="artists"
      :filter-title="'아티스트'"
      :is-working-album="true"
      @detail="toAlbumDetail"
      @create="toCreateAlbum"
      @delete="deleteItem"
      @watchOptions="watchOptions"
      @watchKeyword="watchKeyword"
      @watchFilter="watchFilter"
    />
  </v-container>
</template>
<script>
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import router, { routeName } from '@/router'
  import CommonTable from '@/components/common/table/CommonTable'
  import dataTableMixin from '@/mixins/dataTableMixin'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import tagFormatMixin from '@/mixins/tagFormatMixin'
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'
  import store from '@/store'
  import { role } from '@/assets/enums'
  import albumRoleMixin from '@/mixins/albumRoleMixin'

  export default {
    name: 'Album',
    components: {
      CommonErrorAlert,
      CommonTable,
    },
    mixins: [
      dataTableMixin,
      commonErrorMixin,
      tagFormatMixin,
      calcOffsetMixin,
      albumRoleMixin,
    ],
    async beforeRouteEnter (to, from, next) {
      axios.get(ApiList.countries).then(res => {
        store.commit('setLocaleCodes', res.data.data)
        next()
      })
    },
    data: () => ({
      deleteUrl: ApiList.workingAlbums,
      editedIndex: -1,
      editedItem: {},
      defaultItem: {},
      keyword: '',
      loading: false,
      tableHeader: [
        {
          text: '아티스트',
          value: 'artist',
          sortable: false,
          align: 'center',
        },
        {
          text: '앨범',
          value: 'album',
          sortable: false,
          align: 'center',
        },
        {
          text: '태그',
          value: 'tags',
          sortable: false,
        },
        {
          text: '상태',
          value: 'state',
          sortable: false,
        },
        {
          text: '',
          value: 'actions',
          sortable: false,
        },
      ],
      albumDeleteDialog: false,
      options: {
        page: 1,
        itemsPerPage: 10,
      },
      totalRow: 0,
      artists: [],
      tableData: [],
    }),
    watch: {
      options: {
        handler () {
          this.getTableData()
        },
        deep: true,
      },
      targetFilter: {
        handler () {
          this.getTableData()
        },
        deep: true,
      },
    },
    mounted () {
      this.init()
    },
    methods: {
      handleAllowDelete () {
        return store.state.auth.currentRole === role.CONTENTS_EDITOR
      },
      deleteTargetHandler (item) {
        let res = ''
        if (Object.keys(item).length > 0) {
          res = item.searchTags.join()
        }
        return res
      },
      getDeleteUrl (id) {
        return ApiList.workingAlbumWithId(id)
      },
      async toAlbumDetail (item) {
        const idx = this.tableData.indexOf(item)
        store.commit('setTargetIndex', idx)
        store.commit('setTargetAlbumId', item.id)
        await this.getLocaleCodes()

        // router.push({ name: 'AlbumForm' })
        await router.push({ name: routeName.ALBUM_DETAIL })
      },
      async toCreateAlbum () {
        store.commit('resetTargetItem')
        await this.getLocaleCodes()
        await router.push({ name: routeName.ALBUM_DETAIL })
      },
      async getTableData () {
        this.loadingStart()
        const { itemsPerPage } = this.options
        const params = this.getOffsetParams(itemsPerPage)
        if (this.keyword !== '') params.keyword = this.keyword
        if (this.targetFilter !== '') params.artistId = this.targetFilter

        axios.get(ApiList.workingAlbums, { params }).then(async res => {
          this.loadingDone()
          if (!await this.validateResponse(res)) return
          this.totalRow = res.data.pageInfo.totalElements
          const arr = res.data.data
          for await (const al of arr) {
            const target = this.artists.find(a => a.id === al.artistId)
            if (target) {
              al.artistName = target.name
            }
          }
          this.tableData = arr
        }).catch((err) => {
          this.loadingDone()
          this.setError(err)
        })
      },
      async getArtists () {
        const params = this.getParamsForAll()
        axios.get(ApiList.artists, { params }).then(res => {
          this.artists = res.data.data
        }).catch((err) => {
          this.setError(err)
        })
      },
      async getLocaleCodes () {
        axios.get(ApiList.countries).then(res => {
          if (!this.validateResponse(res)) return
          store.commit('setLocaleCodes', res.data.data)
        }).catch(err => {
          this.setError(err)
        })
      },
      init () {
        this.getArtists()
      },
    },
  }
</script>
<style>
</style>
