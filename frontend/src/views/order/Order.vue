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
      table-title="주문"
      :loading="loading"
      :server="true"
      :total-row="totalRow"
      :origin-options="options"
      :detail="true"
      :delete-url="deleteUrl"
      :is-filtering="true"
      :filter-data="artists"
      :filter-title="'아티스트'"
      @create="getCreate"
      @detail="getDetail"
      @delete="deleteItem"
      @watchOptions="watchOptions"
      @watchFilter="watchFilter"
    />
  </v-container>
</template>
<script>
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import dataTableMixin from '@/mixins/dataTableMixin'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import CommonTable from '@/components/common/table/CommonTable'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'
  import store from '@/store'
  import { routeName } from '@/router'

  export default {
    name: 'Order',
    components: {
      CommonTable,
      CommonErrorAlert,
    },
    mixins: [
      dataTableMixin,
      commonErrorMixin,
      calcOffsetMixin,
    ],
    data: () => ({
      deleteUrl: ApiList.orders,
      totalRow: 0,
      regions: [],
      loading: false,
      keyword: '',
      editedIndex: -1,
      editedItem: {},
      defaultItem: {},
      dialog: false,
      dialogDelete: false,
      artists: [],
      options: {
        page: 1,
        itemsPerPage: 10,
      },
      tableHeader: [
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
          text: '카드',
          value: 'cards',
          sortable: false,
        },
        {
          text: '설명',
          value: 'comment',
          sortable: false,
        },
        {
          text: '생산량',
          value: 'orderQuantity',
          width: 100,
          sortable: false,
        },
        {
          text: '상태',
          value: 'status.description',
          width: 100,
          sortable: false,
        },
        {
          text: '서비스 지역',
          value: 'region.description',
          width: 120,
          sortable: false,
        },
        {
          text: '',
          value: 'actions',
          width: 120,
          sortable: false,
        },
      ],
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
      this.getArtists()
    },
    methods: {
      getCreate () {
        store.commit('resetTargetItem')
        this.$router.push({ name: routeName.ORDER_FORM })
      },
      getDetail (item) {
        const idx = this.tableData.indexOf(item)
        store.commit('setTargetItem', item)
        store.commit('setTargetIndex', idx)
        this.$router.push({ name: routeName.ORDER_FORM })
      },
      async getRegions () {
        axios.get(ApiList.regions).then(res => {
          if (!this.validateResponse(res)) return
          this.regions = res.data.data
        })
      },
      async getTableData () {
        this.loadingStart()
        const { itemsPerPage } = this.options
        const params = this.getOffsetParams(itemsPerPage)
        if (this.keyword !== '') params.keyword = this.keyword
        if (this.targetFilter !== '') params.artistId = this.targetFilter

        axios.get(ApiList.orders, { params }).then(res => {
          this.loadingDone()
          if (!this.validateResponse(res)) return
          this.tableData = res.data.data
          this.totalRow = res.data.pageInfo.totalElements
        }).catch(err => {
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
    },
  }
</script>
<style>
</style>
