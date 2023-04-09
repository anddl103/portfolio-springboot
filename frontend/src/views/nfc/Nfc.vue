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
      :table-data="tableData"
      :table-header="tableHeader"
      :table-title="'NFC 카드'"
      :loading="loading"
      :total-row="totalRow"
      :delete-url="deleteUrl"
      :server="true"
      :search="true"
      :detail="true"
      :is-filtering="true"
      :filter-data="artists"
      :filter-title="'아티스트'"
      @create="getCreate"
      @detail="getDetail"
      @delete="deleteItem"
      @watchOptions="watchOptions"
      @watchKeyword="watchKeyword"
      @watchFilter="watchFilter"
    />
  </v-container>
</template>
<script >
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import dataTableMixin from '@/mixins/dataTableMixin'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import CommonTable from '@/components/common/table/CommonTable'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'
  import tagFormatMixin from '@/mixins/tagFormatMixin'
  import store from '@/store'
  import { routeName } from '@/router'
  import { common } from '@/assets/enums'

  export default {
    name: 'Nfc',
    components: {
      CommonTable,
      CommonErrorAlert,
    },
    mixins: [
      dataTableMixin,
      commonErrorMixin,
      calcOffsetMixin,
      tagFormatMixin,
    ],
    data: () => ({
      deleteUrl: ApiList.nfcs,
      dialogValid: true,
      error: false,
      errorMsg: '',
      cards: [],
      editedItem: {},
      editedIndex: -1,
      loading: false,
      selectedCards: [],
      options: {
        page: 1,
        itemsPerPage: 10,
      },
      totalRow: 0,
      albums: [],
      artists: [],
    }),
    watch: {
      options: {
        async handler () {
          if (this.artists.length === 0) {
            await this.getArtists()
          }
          await this.getTableData()
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
    created () {
      this.pageInit()
    },
    methods: {
      getCreate () {
        store.commit('resetTargetItem')
        this.$router.push({ name: routeName.NFC_FORM })
      },
      getDetail (item) {
        const idx = this.tableData.indexOf(item)
        store.commit('setTargetItem', item)
        store.commit('setTargetIndex', idx)
        this.$router.push({ name: routeName.NFC_FORM })
      },
      removeCard (item) {
        const idx = this.selectedCards.findIndex(c => c.id === item.value.id)
        if (idx >= 0) this.selectedCards.splice(idx, 1)
      },
      async deleteItemConfirm () {
        this.tableData.splice(this.editedIndex, 1)
        this.closeDelete()
      },
      async getTableData () {
        if (this.albums.length < 0) return
        this.loadingStart()
        const { itemsPerPage } = this.options
        const params = this.getOffsetParams(itemsPerPage)
        if (this.keyword !== common.EMPTY) params.keyword = this.keyword
        if (this.targetFilter !== common.EMPTY) params.artistId = this.targetFilter

        await axios.get(ApiList.nfcs, { params }).then(async res => {
          this.loadingDone()
          if (!await this.validateResponse(res)) return
          this.totalRow = res.data.pageInfo.totalElements
          this.tableData = res.data.data
        }).catch(err => {
          this.loadingDone()
          this.setError(err)
        })
      },
      async getArtists () {
        const params = this.getParamsForAll()
        await axios.get(ApiList.artists, { params }).then(async res => {
          store.commit('setArtists', res.data.data)
          this.artists = res.data.data
        }).catch((err) => {
          this.setError(err)
        })
      },
      async pageInit () {
        this.tableHeader = [
          {
            text: '아티스트',
            value: 'artist',
            sortable: false,
          },
          {
            text: '앨범',
            value: 'album',
            sortable: false,
          },
          {
            text: '하위 카드',
            value: 'cards',
            sortable: false,
            cellClass: 'pa-4',
          },
          {
            text: '태그',
            value: 'tags',
            sortable: false,
          },
          {
            text: '설명',
            value: 'comment',
            sortable: false,
          },
          {
            text: '상태',
            value: 'status.description',
            sortable: false,
          },
          {
            text: '',
            value: 'actions',
            sortable: false,
          },
        ]
        this.defaultItem = {
          id: '',
          searchTags: [],
          comment: '',
          cardIds: [],
        }
        this.defaultIndex = -1
        this.editedItem = JSON.parse(JSON.stringify(this.defaultItem))
      },
    },
  }
</script>
<style>
</style>
