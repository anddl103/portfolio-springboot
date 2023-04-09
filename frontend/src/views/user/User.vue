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
      table-title="유저"
      :detail="true"
      :modify="false"
      :loading="loading"
      :server="true"
      :search="true"
      :total-row="totalRow"
      :allow-create="false"
      :allow-delete="false"
      @detail="toUserDetail"
      @watchOptions="watchOptions"
      @watchKeyword="watchKeyword"
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
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'
  import store from '@/store'

  export default {
    name: 'User',
    components: {
      CommonErrorAlert,
      CommonTable,
    },
    mixins: [
      dataTableMixin,
      commonErrorMixin,
      calcOffsetMixin,
    ],
    data: () => ({
      editedIndex: -1,
      editedItem: {},
      defaultItem: {},
      keyword: '',
      loading: false,
      tableHeader: [
        {
          text: 'uid',
          value: 'uid',
          width: 200,
        },
        {
          text: '이메일',
          value: 'userEmail',
          width: 200,
        },
        {
          text: '프로바이더',
          value: 'providers',
          width: 200,
        },
        {
          text: '가입(탈퇴)일시',
          value: 'datetime',
          width: 200,
        },
      ],
      options: {
        page: 1,
        itemsPerPage: 10,
      },
      totalRow: 0,
      tableData: [],
    }),
    watch: {
      options: {
        handler () {
          this.getTableData()
        },
        deep: true,
      },
    },
    methods: {
      toUserDetail (item) {
        // FIXME
        store.commit('setTargetItem', item)
        router.push({ name: routeName.USER_FORM, params: { target: item.id } })
      },
      async getTableData () {
        this.loadingStart()
        const { itemsPerPage } = this.options
        const params = this.getOffsetParams(itemsPerPage)
        if (this.keyword !== '') {
          params.keyword = this.keyword
        }
        axios.get(ApiList.users, { params }).then(async res => {
          this.loadingDone()
          if (!await this.validateResponse(res)) return
          this.totalRow = res.data.pageInfo.totalElements
          this.tableData = res.data.data
        }).catch((err) => {
          this.loadingDone()
          this.setError(err)
        })
      },
    },
  }
</script>
<style>
</style>
