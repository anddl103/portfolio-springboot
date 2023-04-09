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
      table-title="다국어"
      :loading="loading"
      :server="true"
      :origin-options="options"
      :total-row="totalRow"
      :allow-create="false"
      :allow-delete="false"
      :modify="false"
      :search="true"
      :keyword="keyword"
      search-label="한국어"
      @delete="deleteItem"
      @watchKeyword="watchKeyword"
      @watchOptions="watchOptions"
    />

    <v-divider class="my-15" />
    <v-row>
      <v-col
        offset="1"
        cols="10"
      >
        <v-card>
          <v-row>
            <v-col
              class="ma-auto"
              cols="3"
            >
              파일 업로드
            </v-col>
            <v-col
              cols="4"
            >
              <v-file-input
                ref="inputFile"
                v-model="file"
                accept=".csv"
                placeholder="CSV 파일 업로드"
                label="*.CSV"
                truncate-length="100"
                @change="fileValidate"
              />
            </v-col>
            <v-col cols="4">
              <v-btn
                v-if="handlePermission"
                @click="upload"
              >
                upload
              </v-btn>
            </v-col>
          </v-row>
          <v-row>
            <v-col
              class="ma-auto"
              cols="3"
            >
              파일 다운로드
            </v-col>
            <v-col cols="8">
              <v-btn @click="download">
                download
              </v-btn>
            </v-col>
          </v-row>
        </v-card>
      </v-col>
    </v-row>
    <dialog-form
      :open="dialog"
      :target-index="editedIndex"
      :target-item="editedItem"
      form-title="수정"
      :table-data="tableData"
      @close="close"
      @afterSave="save"
      @afterUpdate="update"
    />
  </v-container>
</template>
<script >
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import store from '@/store'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import CommonTable from '@/components/common/table/CommonTable'
  import DialogForm from './LanguageForm'
  import dataTableMixin from '@/mixins/dataTableMixin'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'
  import { role } from '@/assets/enums'

  export default {
    name: 'Language',
    components: {
      CommonErrorAlert,
      DialogForm,
      CommonTable,
    },
    mixins: [
      dataTableMixin,
      commonErrorMixin,
      calcOffsetMixin,
    ],
    data: () => ({
      formats: [
        'csv',
      ],
      file: null,
      loading: false,
      keyword: '',
      editedIndex: -1,
      editedItem: {
        id: '',
        values: {},
      },
      defaultItem: {
        id: '',
        values: {},
      },
      dialog: false,
      localeCodes: [],
      options: {
        page: 1,
        itemsPerPage: 10,
      },
      totalRow: 0,
    }),
    computed: {
      handlePermission () {
        return store.state.auth.currentRole === role.CONTENTS_EDITOR
      },
    },
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
      fileValidate () {
        if (this.file !== null && this.file !== undefined) {
          const ext = this.file.name.slice(this.file.name.lastIndexOf('.') + 1)
          if (!this.formats.includes(ext)) {
            this.error = true
            this.errorMsg = 'csv 확장자만 가능합니다.'
            this.$refs.inputFile.reset()
          } else {
            this.error = false
          }
        }
      },
      update (newData) {
        this.tableData.splice(this.editedIndex, 1, newData)
      },
      save (data) {
        this.tableData.push(data)
      },
      async getTableData () {
        this.loadingStart()
        const { itemsPerPage } = this.options
        const params = this.getOffsetParams(itemsPerPage)
        if (this.keyword !== '') {
          params.keyword = this.keyword
        }
        axios.get(ApiList.languagePacks, { params }).then(res => {
          this.loadingDone()
          if (!this.validateResponse(res)) return
          this.totalRow = res.data.pageInfo.totalElements
          this.tableData = res.data.data
        }).catch(err => {
          this.loadingDone()
          this.setError(err)
        })
      },
      async download () {
        await store.dispatch('setLoadingTrue')
        window.location.href = ApiList.downloadLanguagePack
        await store.dispatch('setLoadingFalse')
      },
      async upload () {
        if (this.file === null) return
        this.fileValidate()
        await store.dispatch('setLoadingTrue')
        const formData = new FormData()
        formData.append('file', this.file)
        axios.post(ApiList.uploadLanguagePack, formData).then(res => {
          this.validateResponse(res)
        }).catch(err => this.setError(err))
      },
      async setTableHeader () {
        const arr = [
          {
            text: '',
            value: 'actions',
            sortable: false,
          },
        ]
        for await (const cc of this.localeCodes) {
          const obj = {
            text: cc.comment,
            value: 'values.' + cc.code.toLowerCase(),
            sortable: false,
          }
          arr.splice(-1, 0, obj)
        }
        this.tableHeader = arr
      },
      async getLocaleCodes () {
        this.localeCodes = await axios.get(ApiList.countries).then(res => {
          if (!this.validateResponse(res)) return
          let arr = res.data.data
          arr = arr.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
          store.commit('setLocaleCodes', arr)
          return arr
        }).catch(err => {
          this.setError(err)
        })
      },
      async init () {
        await this.getLocaleCodes()
        await this.setTableHeader()
      },
    },
  }
</script>
<style>
</style>
