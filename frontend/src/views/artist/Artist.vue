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
      table-title="아티스트"
      :thumbnail="true"
      :loading="loading"
      :server="true"
      :total-row="totalRow"
      :sort-dialog="true"
      :delete-url="deleteUrl"
      :detail="true"
      @create="getCreate"
      @detail="getDetail"
      @delete="deleteItem"
      @watchOptions="watchOptions"
      @syncOrder="syncOrder"
    />
  </v-container>
</template>
<script >
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import CommonTable from '@/components/common/table/CommonTable'
  import dataTableMixin from '@/mixins/dataTableMixin'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import store from '@/store'
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'
  import { routeName } from '@/router'

  export default {
    name: 'Artist',
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
      deleteUrl: ApiList.artists,
      tableHeader: [
        {
          text: '아티스트',
          value: 'artist',
          sortable: false,
          sortTableHeader: true,
          align: 'center',
        },
        {
          text: '표시순서',
          value: 'sortOrder',
          sortable: false,
        },
        {
          text: '공개 여부',
          value: 'display',
          sortable: false,
        },
        {
          text: '',
          value: 'actions',
          sortable: false,
        },
      ],
      targetIndex: -1,
      targetItem: {
        id: '',
        name: '',
      },
      dialog: false,
      dialogDelete: false,
      options: {
        page: 1,
        itemsPerPage: 10,
      },
      loading: false,
      tableData: [],
      totalRow: 0,
      localeCodes: [],
    }),
    watch: {
      options: {
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
      async getDetail (item) {
        axios.get(ApiList.artistWithId(item.id)).then(res => {
          if (!this.validateResponse(res)) return
          const data = {
            idx: this.tableData.indexOf(item),
            target: res.data.data,
          }
          store.dispatch('setTarget', data).then(() => {
            this.$router.push({ name: routeName.ARTIST_FORM })
          })
        }).catch((err) => {
          this.setError(err)
        })
      },
      getCreate () {
        store.commit('resetTargetItem')
        this.$router.push({ name: routeName.ARTIST_FORM })
      },
      watchOptions (options) {
        this.options = options
      },
      syncOrder (arr) {
        const orderList = []
        for (const data of arr) {
          const rd = {
            artistId: data.id,
            sortOrder: data.sortOrder,
          }
          orderList.push(rd)
        }
        const request = {
          orderList: orderList,
        }
        axios.patch(ApiList.artistSort, request).then(async res => {
          if (!await this.validateResponse(res)) return
          await this.getTableData()
        }).catch((err) => {
          this.setError(err)
        })
      },
      deleteItemConfirm () {
        this.tableData.splice(this.editedIndex, 1)
        this.closeDelete()
      },
      getTableData () {
        this.loadingStart()
        const params = this.getOffsetParams(this.options.itemsPerPage)
        axios.get(ApiList.artists, { params }).then(res => {
          this.loadingDone()
          if (!this.validateResponse(res)) return
          res.data.data.sort((a, b) => a.sortOrder - b.sortOrder)
          res.data.data.sort((a, b) => b.display - a.display)
          this.tableData = res.data.data
          this.totalRow = res.data.pageInfo.totalElements
        }).catch((err) => {
          this.loadingDone()
          this.setError(err)
        })
      },
      async getLocaleCodes () {
        axios.get(ApiList.countries).then(res => {
          if (!this.validateResponse(res)) return
          store.commit('setLocaleCodes', res.data.data)
          this.localeCodes = res.data.data
        }).catch(err => {
          this.setError(err)
        })
      },
      init () {
        this.getLocaleCodes()
      },
    },
  }
</script>
<style>
</style>
