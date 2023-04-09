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

    <v-row class="ma-auto">
      <v-col
        cols="2"
      >
        아티스트 정보
      </v-col>
      <v-col cols="8">
        <v-form
          ref="form"
          v-model="formValid"
          lazy-validation
        >
          <v-row>
            <v-text-field
              v-model="targetItem.name"
              :rules="[required]"
              :readonly="!editMode"
              label="그룹명"
              :class="!editMode ? 'disable-events' : ''"
            />
          </v-row>
          <v-row>
            <common-add-language-table
              table-title="멤버"
              :table-data="tableData"
              :table-header="tableHeader"
              :rules="[required]"
              :readonly="!editMode"
              @addRow="addRow"
              @removeRow="removeRow"
              @sort="sortData"
            />
          </v-row>
          <v-row>
            <v-switch
              v-model="targetItem.display"
              inset
              :label="targetItem.display ? '공개' : '비공개'"
              :readonly="!editMode"
              :class="!editMode ? 'disable-events' : ''"
            />
          </v-row>
        </v-form>
      </v-col>
    </v-row>
    <v-divider
      class="mt-3 mb-10"
    />
    <v-row class="ma-auto">
      <v-col cols="2">
        파일 업로드
      </v-col>
      <v-col cols="8">
        <v-row>
          <file-upload
            v-for="obj in uploadedFiles"
            :ref="obj.refname"
            :key="obj.refname"
            class="ma-3"
            :file-info="obj"
            :width="150"
            :height="150"
            :valid="obj.isValid"
            :readonly="!editMode"
          />
        </v-row>
      </v-col>
    </v-row>
    <v-divider
      class="mt-3 mb-10"
    />
    <button-handler
      cancel-destination="Artist"
      :edit-mode="editMode"
      :target-index="targetIndex"
      @save="save"
      @update="update"
      @modify="toModify"
      @cancelModify="cancelModify"
    />
  </v-container>
</template>
<script >
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import store from '@/store'
  import FileUpload from '@/components/common/FileUpload'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import CommonAddLanguageTable from '@/components/common/CommonAddLanguageTable'
  import commonAddLanguageTableMixin from '@/mixins/commonAddLanguageTableMixin'
  import fileUploadMixin from '@/mixins/fileUploadMixin'
  import rulesMixin from '@/mixins/rulesMixin'
  import commonFormMixin from '@/mixins/commonFormMixin'
  import ButtonHandler from '@/components/common/ButtonHandler'
  import { uploadType, ruleType, common, errorMsg, s3PathConst } from '@/assets/enums'
  const constant = Object.freeze({
    slotText: {
      THUMBNAIL: '썸네일',
      LOGO: '로고',
    },
  })

  export default {
    name: 'ArtistForm',
    components: {
      FileUpload,
      CommonErrorAlert,
      CommonAddLanguageTable,
      ButtonHandler,
    },
    mixins: [
      commonErrorMixin,
      commonAddLanguageTableMixin,
      fileUploadMixin,
      rulesMixin,
      commonFormMixin,
    ],
    data: () => ({
      dialogValid: true,
      model: null,
      localeCodes: store.state.common.localeCodes,
      tableData: [],
      memberLocaleIds: [],
      uploadedFiles: [],
      editMode: false,
      isDetail: true,
      formValid: true,
      targetItem: JSON.parse(JSON.stringify(store.state.common.targetItem)),
    }),
    mounted () {
      this.init()
    },
    methods: {
      init () {
        this.setInitUploadedFiles()
        this.editMode = this.targetIndex === -1
        this.isDetail = this.targetIndex >= 0
        if (this.targetIndex !== -1) {
          this.tableData = this.targetItem.members
        }
      },
      sortData (data) {
        for (let i = 0; i < data.length; i++) {
          this.tableData.splice(i, 1, data[i])
        }
      },
      async toModify () {
        this.editMode = true
        this.defaultUploadedFiles = JSON.parse(JSON.stringify(this.uploadedFiles))
      },
      async cancelModify () {
        this.targetItem = JSON.parse(JSON.stringify(this.defaultItem))
        this.tableData = this.targetItem.members
        this.uploadedFiles = JSON.parse(JSON.stringify(this.defaultUploadedFiles))
        this.isDetail = true
        this.editMode = false
      },
      async duplicateCheck () {
        const params = {
          name: this.targetItem.name,
        }
        if (this.targetIndex >= 0) {
          params.id = this.targetItem.id
        }
        return axios.get(ApiList.artistDuplicateCheck, { params }).then(res => {
          if (res.data.code !== 0) {
            this.setError(res.data.message)
            return true
          }
          // true : duplicated (NG), false : NOT duplicated (OK)
          return res.data.data.duplicated
        }).catch(err => {
          this.setError(err.message)
        })
      },
      checkForm () {
        return this.$refs.form.validate()
      },
      async validation () {
        this.resetError()
        const con1 = this.checkForm()
        const con2 = this.checkFilesValid(this.uploadedFiles)
        let con3 = false
        if (con1 && con2) {
          con3 = await this.duplicateCheck().then(res => {
            // TRUE : duplicated ( => do not pass true)
            if (res) {
              this.setError(errorMsg.DUPLICATED_ARTIST_NAME)
            }
            return true
          })
          return con3
        }
        return con1 && con2 && con3
      },
      async save () {
        await store.dispatch('setLoadingTrue')
        if (!await this.validation()) return

        const request = {
          name: this.targetItem.name,
        }
        axios.post(ApiList.artists, request).then(async res => {
          if (await this.validateResponse(res)) {
            this.targetItem.id = res.data.data.id
            await this.update(true)
            // index >= 0 일 때 저장버튼이 업데이트를 실행하도록 되어있기 때문에 임의로 인덱스를 높여준다.
            this.targetIndex = 999
          }
        }).catch(err => {
          this.setError(err.message)
        })
      },
      async updateMembers () {
        this.memberLocaleIds = []
        const arr = this.tableData
        // id 없으면 신규등록 (obj.name => 다국어 id)
        const insertTargets = arr.filter(m => m.id === null)
        for await (const mem of insertTargets) {
          const req = {
            values: mem.values,
          }
          await axios.post(ApiList.languagePacks, req).then(res => {
            if (res.data.code !== 0) {
              this.setError(res.data.message)
              return
            }
            const value = res.data.data
            this.memberLocaleIds.push(value.id)
          }).catch(err => {
            this.setError(err.message)
          })
        }
        const oldMembers = arr.filter(m => m.id !== null)
        const originMembers = this.defaultItem.members ? this.defaultItem.members : []
        // origin 에 있지만 old 에 없으면 삭제대상
        const deleteTargets = []
        for await (const origin of originMembers) {
          const target = oldMembers.find(o => o.id === origin.id)
          if (target === undefined) {
            deleteTargets.push(origin.name)
          }
        }
        if (deleteTargets.length > 0) {
          axios.post(ApiList.deleteMembers, deleteTargets).then(res => {
            if (res.data.code !== 0) {
              this.setError(res.data.message)
            }
          }).catch(err => {
            this.setError(err.message)
          })
        }

        // origin 에 있고 old 에도 있으면 갱신대상
        const updateTargets = oldMembers.filter(om => originMembers.find(old => old.id === om.id))
        for await (const updateTarget of updateTargets) {
          if (originMembers.find(om => om.id === updateTarget.id) !== updateTarget) {
            const req = {
              values: updateTarget.values,
            }
            await axios.patch(ApiList.languageWithId(updateTarget.name), req).then(res => {
              if (res.data.code !== 0) {
                this.setError(res.data.message)
                return
              }
              const value = res.data.data
              this.memberLocaleIds.push(value.id)
            }).catch(err => {
              this.setError(err.message)
            })
          } else {
            this.memberLocaleIds.push(updateTarget.name)
          }
        }
        // 모든 멤버가 잘 저장 된 경우 true, 아니면 false 를 리턴
        return this.memberLocaleIds.length === this.tableData.length
      },
      getUploadPath () {
        const pathArr = []
        pathArr.push(s3PathConst.ARTISTS)
        pathArr.push(this.targetItem.id)
        return pathArr.join(s3PathConst.DELIMITER)
      },
      async update (fromSave) {
        await store.dispatch('setLoadingTrue')
        if (fromSave === undefined) {
          if (!await this.validation()) {
            if (this.$store.state.common.loading) {
              await store.dispatch('setLoadingTrue')
            }
            return
          }
        }
        if (!await this.updateMembers()) {
          this.setError(errorMsg.FAILED_MODIFY_MEMBER)
          return false
        }
        if (!await this.uploadFiles(this.getUploadPath())) {
          this.setError(errorMsg.ERROR_WHILE_UPLOADING)
          return false
        }
        const members = []
        for await (const mid of this.memberLocaleIds) {
          const member = this.tableData.find(td => td.name === mid)
          const data = {
            id: member === undefined ? null : member.id === undefined ? null : member.id,
            name: mid,
          }
          members.push(data)
        }
        const request = {
          name: this.targetItem.name,
          members: members,
          display: this.targetItem.display,
          thumbnailKey: this.uploadedFiles.find(uf => uf.refname === uploadType.THUMBNAIL).key,
          logoKey: this.uploadedFiles.find(uf => uf.refname === uploadType.LOGO).key,
        }
        axios.patch(ApiList.artistWithId(this.targetItem.id), request)
          .then(res => {
            if (!this.validateResponse(res)) return
            this.defaultItem = res.data.data
            this.targetItem = JSON.parse(JSON.stringify(this.defaultItem))
            this.editMode = false
            this.isDetail = true
          }).catch(err => {
            this.setError(err)
          })
      },
      setInitUploadedFiles () {
        this.uploadedFiles = [
          {
            refname: uploadType.THUMBNAIL,
            model: null,
            accept: this.imageAccept,
            slotText: constant.slotText.THUMBNAIL,
            key: this.targetItem.thumbnailKey !== undefined ? this.targetItem.thumbnailKey : common.EMPTY,
            isKey: false,
            isVideo: false,
            isValid: true,
            rules: [ruleType.REQUIRED, ruleType.IMAGE],
            dimension: {
              x: 0,
              y: 0,
            },
          },
          {
            refname: uploadType.LOGO,
            model: null,
            accept: this.imageAccept,
            slotText: constant.slotText.LOGO,
            key: this.targetItem.logoKey !== undefined ? this.targetItem.logoKey : common.EMPTY,
            isKey: false,
            isVideo: false,
            isValid: true,
            rules: [ruleType.REQUIRED, ruleType.IMAGE],
            dimension: {
              x: 0,
              y: 0,
            },
          },
        ]
      },
    },
  }
</script>
<style>
</style>
