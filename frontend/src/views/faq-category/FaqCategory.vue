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
      table-title="FAQ 카테고리"
      :loading="loading"
      :hide-footer="true"
      :delete-url="deleteUrl"
      :allow-create="true"
      :allow-delete="true"
      :popup="true"
      @create="editItem"
      @edit="editItem"
      @delete="deleteItem"
      @watchKeyword="watchKeyword"
    />

    <dialog-form
      :open.sync="dialog"
      @submit="getTableData"
    />
  </v-container>
</template>
<script >
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import CommonTable from '@/components/common/table/CommonTable'
  import DialogForm from './FaqCategoryForm'
  import dataTableMixin from '@/mixins/dataTableMixin'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import store from '@/store'

  export default {
    name: 'FaqCategory',
    components: {
      CommonErrorAlert,
      DialogForm,
      CommonTable,
    },
    mixins: [
      dataTableMixin,
      commonErrorMixin,
    ],
    data: () => ({
      deleteUrl: ApiList.faqCategories,
      localeCodes: store.state.common.localeCodes,
      loading: false,
      dialog: false,
      dialogValid: true,
    }),
    beforeRouteEnter (to, from, next) {
      axios.get(ApiList.countries).then(res => {
        const localeCodes = res.data.data.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
        store.commit('setLocaleCodes', localeCodes)
        next()
      }).catch(() => {
        next(from.path)
      })
    },
    mounted () {
      this.init()
    },
    methods: {
      async resetForm () {
        store.commit('resetTargetItem')
      },
      async getTableData () {
        this.loadingStart()
        axios.get(ApiList.faqCategories).then(res => {
          this.loadingDone()
          if (!this.validateResponse(res)) return
          this.tableData = res.data.data
        }).catch(err => {
          this.loadingDone()
          this.setError(err)
        })
      },
      async createHeader () {
        this.tableHeader = [
          {
            text: '설명',
            value: 'comment',
            sortable: false,
            align: 'start',
          },
          {
            text: '추가일시',
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
        for (const [idx, lc] of Object(this.localeCodes).entries()) {
          const obj = {
            text: lc.comment,
            value: 'localeCodeSubject.' + lc.code + '.subject',
            sortable: false,
          }
          this.tableHeader.splice(idx, 0, obj)
        }
      },
      async init () {
        await this.createHeader()
        await this.getTableData()
      },
    },
  }
</script>
<style>
</style>
