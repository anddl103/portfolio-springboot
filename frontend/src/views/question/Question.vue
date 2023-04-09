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
      table-title="1:1 문의"
      :thumbnail="true"
      :detail="true"
      :modify="false"
      :loading="loading"
      :server="true"
      :search="true"
      :total-row="totalRow"
      :delete-url="deleteUrl"
      @detail="toQuestionDetail"
      @create="toCreateQuestion"
      @delete="deleteItem"
      @watchOptions="watchOptions"
      @watchKeyword="watchKeyword"
    />

    <question-add-dialog
      :open="dialogCreate"
      @close="closeCreateDialog"
      @save="createItemConfirm"
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
  import QuestionAddDialog from './QuestionAddDialog'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'
  import store from '@/store'

  export default {
    name: 'Question',
    components: {
      CommonTable,
      QuestionAddDialog,
      CommonErrorAlert,
    },
    mixins: [
      dataTableMixin,
      commonErrorMixin,
      calcOffsetMixin,
    ],
    data: () => ({
      deleteUrl: ApiList.questions,
      keyword: '',
      loading: false,
      tableHeader: [
        {
          text: '유저',
          value: 'createdBy',
          width: 200,
        },
        {
          text: '타이틀',
          value: 'questionTitle',
          width: 200,
        },
        {
          text: '문의 시각',
          value: 'createdAt',
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
      dialogCreate: false,
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
      toQuestionDetail (item) {
        const idx = this.tableData.indexOf(item)
        store.commit('setTargetItem', item)
        store.commit('setTargetIndex', idx)
        router.push({ name: routeName.QUESTION_FORM })
      },
      async getTableData () {
        this.loadingStart()
        const { itemsPerPage } = this.options
        const params = this.getOffsetParams(itemsPerPage)
        if (this.keyword !== '') {
          params.keyword = this.keyword
        }
        axios.get(ApiList.questions, { params }).then(async res => {
          this.loadingDone()
          if (!await this.validateResponse(res)) return

          this.totalRow = res.data.pageInfo.totalElements
          this.tableData = res.data.data
        }).catch((err) => {
          this.loadingDone()
          this.setError(err)
        })
      },

      // TODO: 질문 하는 로직은 삭제.
      toCreateQuestion () {
        this.dialogCreate = true
      },
      closeCreateDialog () {
        this.dialogCreate = false
      },
      createItemConfirm () {
        this.getTableData()
        this.closeCreateDialog()
      },
    },
  }
</script>
<style>
</style>
