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
      table-title="국가 코드"
      :loading="loading"
      :hide-footer="true"
      :delete-url="deleteUrl"
      @create="editItem"
      @edit="editItem"
      @delete="deleteItem"
      @watchKeyword="watchKeyword"
    />

    <dialog-form
      :open="dialog"
      :table-data="tableData"
      @close="close"
      @afterSave="getTableData"
      @afterUpdate="getTableData"
    />
  </v-container>
</template>
<script >
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import CommonTable from '@/components/common/table/CommonTable'
  import DialogForm from './CountryCodeForm'
  import dataTableMixin from '@/mixins/dataTableMixin'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import store from '@/store'

  export default {
    name: 'CountryCode',
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
      deleteUrl: ApiList.countries,
      loading: false,
      dialog: false,
      dialogValid: true,
    }),
    mounted () {
      this.init()
    },
    methods: {
      async resetForm () {
        store.commit('resetTargetItem')
      },
      async getTableData () {
        this.loadingStart()
        await this.resetForm()
        axios.get(ApiList.countries).then(res => {
          this.loadingDone()
          if (!this.validateResponse(res)) return
          this.tableData = res.data.data
        }).catch(err => {
          this.loadingDone()
          this.setError(err)
        })
      },
      init () {
        this.getTableData()
        this.tableHeader = [
          {
            text: '코드',
            value: 'code',
            sortable: false,
            width: 100,
            align: 'center',
          },
          {
            text: '설명',
            value: 'comment',
            sortable: false,
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
      },
    },
  }
</script>
<style>
</style>
