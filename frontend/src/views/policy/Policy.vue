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
      table-title="정책"
      :thumbnail="true"
      :detail="true"
      :modify="false"
      :loading="loading"
      :server="true"
      :search="false"
      search-label="제목"
      :total-row="totalRow"
      :delete-url="deleteUrl"
      @create="toCreate"
      @detail="toDetail"
      @delete="deleteItem"
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
  import store from '@/store'
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'

  export default {
    name: 'Policy',
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
      deleteUrl: ApiList.policies,
      keyword: '',
      loading: false,
      tableHeader: [
        {
          text: '사용처',
          value: 'usage',
        },
        {
          text: '타이틀',
          value: 'title',
          sortable: false,
        },
        {
          text: '생성일시',
          value: 'createdAt',
        },
        {
          text: '버전',
          value: 'version',
        },
        {
          text: '',
          value: 'actions',
        },
      ],
      options: {
        page: 1,
        itemsPerPage: 10,
      },
      totalRow: 0,
      tableData: [],
      localeCodes: store.state.common.localeCodes,
    }),
    watch: {
      options: {
        handler () {
          this.getTableData()
        },
        deep: true,
      },
    },
    beforeRouteEnter (to, from, next) {
      axios.get(ApiList.countries).then(res => {
        const localeCodes = res.data.data.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
        store.commit('setLocaleCodes', localeCodes)
        next()
      }).catch(err => {
        this.setError(err)
        next(from.path)
      })
    },
    methods: {
      toCreate () {
        router.push({ name: routeName.POLICY_FORM })
      },
      toDetail (item) {
        const idx = this.tableData.indexOf(item)
        store.commit('setTargetItem', item)
        store.commit('setTargetIndex', idx)
        router.push({ name: routeName.POLICY_FORM })
      },
      async getTableData () {
        this.loadingStart()
        const { itemsPerPage } = this.options
        const params = this.getOffsetParams(itemsPerPage)
        if (this.keyword !== '') {
          params.keyword = this.keyword
        }
        axios.get(ApiList.policies, { params }).then(async res => {
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
