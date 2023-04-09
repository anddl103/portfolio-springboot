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
      table-title="배치"
      :thumbnail="true"
      :detail="false"
      :modify="false"
      :loading="loading"
      :server="true"
      :total-row="totalRow"
      :is-toolbar-button-toggle="true"
      :is-batch="true"
      :allow-create="false"
      :is-working-album="true"
      :batch-done.sync="batchDone"
      @watchOptions="watchOptions"
    />
  </v-container>
</template>
<script>
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import CommonTable from '@/components/common/table/CommonTable'
  import dataTableMixin from '@/mixins/dataTableMixin'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import store from '@/store'
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'

  export default {
    name: 'Batch',
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
      loading: false,
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
          text: '상태',
          value: 'state',
          sortable: false,
          width: 150,
        },
        {
          text: '갱신일시',
          value: 'updatedAt',
          sortable: false,
          width: 200,
        },
      ],
      options: {
        page: 1,
        itemsPerPage: 10,
      },
      totalRow: 0,
      tableData: [],
      batchDone: false,
    }),
    watch: {
      batchDone () {
        this.getTableData()
      },
    },
    mounted () {
      this.init()
    },
    beforeRouteEnter (to, from, next) {
      const url = ApiList.batchState
      axios.get(url).then(res => {
        store.commit('setBatchState', res.data.data.batchState)
        next()
      }).catch(err => {
        this.setError(err)
        next(from.path)
      })
    },
    methods: {
      async init () {
        await this.getTableData()
      },
      async getTableData () {
        const { itemsPerPage } = this.options
        const params = this.getOffsetParams(itemsPerPage)
        axios.get(ApiList.batches, { params }).then(res => {
          this.tableData = res.data.data
          this.totalRow = res.data.pageInfo.totalElements
        }).catch(err => {
          this.setError(err)
        })
      },
    },
  }
</script>
<style>
</style>
