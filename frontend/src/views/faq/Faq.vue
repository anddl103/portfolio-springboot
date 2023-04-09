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
      :table-data.sync="tableData"
      :table-header="tableHeader"
      table-title="FAQ"
      :thumbnail="true"
      :detail="true"
      :modify="false"
      :loading="loading"
      :server="true"
      :search="false"
      search-label="타이틀"
      :total-row="totalRow"
      :delete-url="deleteUrl"
      :is-filtering="true"
      :filter-data="categories"
      :filter-title="'카테고리'"
      @detail="toDetail"
      @create="toCreate"
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
  import router, { routeName } from '@/router'
  import CommonTable from '@/components/common/table/CommonTable'
  import dataTableMixin from '@/mixins/dataTableMixin'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import store from '@/store'
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'

  export default {
    name: 'Faq',
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
      deleteUrl: ApiList.faqs,
      keyword: '',
      loading: false,
      tableHeader: [],
      tableData: [],
      options: {
        page: 1,
        itemsPerPage: 10,
      },
      totalRow: 0,
      categories: store.state.common.faqCategories,
      locales: [],
    }),
    async beforeRouteEnter (to, from, next) {
      async function getLocaleCodes () {
        await axios.get(ApiList.countries).then(res => {
          if (res.data.code !== 0) {
            next(from.path)
          }
          const localeCodes = res.data.data.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
          store.commit('setLocaleCodes', localeCodes)
        })
      }

      async function getCategories () {
        await axios.get(ApiList.faqCategories).then(res => {
          if (res.data.code !== 0) {
            next(from.path)
          }
          store.commit('setFaqCategories', res.data.data)
        })
      }

      await getLocaleCodes()
      await getCategories()
      next()
    },
    watch: {
      options: {
        handler () {
          this.getTableData()
        },
        deep: true,
      },
      targetFilter () {
        this.getTableData()
      },
    },
    mounted () {
      this.init()
    },
    methods: {
      toCreate () {
        router.push({ name: routeName.FAQ_FORM })
      },
      toDetail (item) {
        const idx = this.tableData.indexOf(item)
        store.commit('setTargetItem', item)
        store.commit('setTargetIndex', idx)
        router.push({ name: 'FaqForm' })
      },
      async getTableData () {
        this.loadingStart()
        const { itemsPerPage } = this.options
        const params = this.getOffsetParams(itemsPerPage)
        if (this.keyword !== '') {
          params.keyword = this.keyword
        }
        if (this.targetFilter !== '') params.artistId = this.targetFilter
        axios.get(ApiList.faqs, { params }).then(async res => {
          this.loadingDone()
          if (!await this.validateResponse(res)) return
          this.totalRow = res.data.pageInfo.totalElements
          this.tableData = res.data.data
          this.setTableHeader()
        }).catch((err) => {
          this.loadingDone()
          this.setError(err)
        })
      },
      setTableHeader () {
        this.tableHeader = [
          {
            text: 'DB 아이디',
            value: 'id',
            sortable: false,
            width: 150,
          },
          {
            text: '카테고리',
            value: 'category',
            sortable: false,
            width: 130,
          },
          {
            text: '언어',
            value: 'localeCode',
            sortable: false,
            width: 110,
            align: 'start',
          },
          {
            text: '타이틀',
            value: 'title',
            sortable: false,
            align: 'start',
          },
          {
            text: '생성일시',
            value: 'createdAt',
            sortable: false,
            width: 200,
            align: 'start',
          },
          {
            text: '',
            value: 'actions',
            sortable: false,
            width: 150,
            align: 'center',
          },
        ]
      },
      async init () {
      },
    },
  }
</script>
<style>
</style>
