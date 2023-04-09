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
    <validation-observer
      ref="obs"
      class="ma-auto"
    >
      <v-row class="ma-auto">
        <v-col
          cols="2"
        >
          배너 정보
        </v-col>
        <v-col cols="8">
          <validation-provider
            ref="artist"
            name="artist"
            :rules="'required'"
          >
            <v-autocomplete
              v-model="selectedArtist"
              slot-scope="{
                errors,
              }"
              :error-messages="errors"
              label="아티스트"
              item-text="name"
              return-object
              required
              :items="artists"
              :readonly="!editMode"
              :append-icon="editMode ? undefined : ''"
              :class="!editMode ? 'disable-events' : ''"
            >
              <template v-slot:selection="{item}">
                <v-avatar left>
                  <v-img
                    :src="getSignedUrl(item.logoKey, false)"
                    max-width="50"
                    max-height="50"
                  />
                </v-avatar>
                <v-divider
                  class="mx-4"
                  inset
                  vertical
                />
                <label class="v-label">{{ item.name }}</label>
              </template>
              <template v-slot:item="{ item }">
                <v-avatar left>
                  <v-img
                    :src="getSignedUrl(item.logoKey, false)"
                    max-width="50"
                    max-height="50"
                  />
                </v-avatar>
                <v-divider
                  class="mx-4"
                  inset
                  vertical
                />
                <label class="v-label">{{ item.name }}</label>
              </template>
            </v-autocomplete>
          </validation-provider>
          <v-row>
            <file-upload
              :ref="uploadedFile.refname"
              class="bg-color-inherit"
              :file-info="uploadedFile"
              :width="150"
              :height="150"
              :readonly="!editMode"
              :valid="uploadedFile.isValid"
            />
            <v-col class="align-self-end">
              <v-row>
                <v-col cols="12">
                  <v-combobox
                    v-model="tags"
                    class="tag-input"
                    label="검색용 태그"
                    multiple
                    chips
                    deletable-chips
                    :readonly="!editMode"
                    :class="!editMode ? 'disable-events' : ''"
                    :append-icon="editMode ? undefined : ''"
                  />
                </v-col>
                <v-col cols="12">
                  <v-switch
                    v-model="displayFlag"
                    inset
                    :label="displayHandler()"
                    :readonly="!editMode"
                    :class="!editMode ? 'disable-events' : ''"
                  />
                </v-col>
              </v-row>
            </v-col>
          </v-row>
        </v-col>
      </v-row>
      <v-divider class="my-3" />
      <v-row class="ma-auto">
        <v-col
          cols="2"
        >
          파일 업로드
        </v-col>
        <v-col cols="8">
          <v-data-table
            :headers="tableHeader"
            :items="tableData"
            hide-default-footer
            :item-class="rowClass"
          >
            <template v-slot:item.language="{ item }">
              <label class="v-label">{{ item.language.comment }}</label>
            </template>
            <template v-slot:item.contents="{ item }">
              <file-upload
                :ref="item.contents.refname"
                :file-info.sync="item.contents"
                :readonly="!editMode"
                :valid="item.contents.isValid"
                @dropAction="imageDropAction"
                @removeAction="imageDropAction"
              />
            </template>
            <template v-slot:item.input="{ item, index }">
              <v-row class="height-webkit-fill align-content-end pb-4">
                <v-col cols="12">
                  <validation-provider
                    :ref="'title' + index"
                    :name="'title' + index"
                    :rules="item.rules"
                  >
                    <v-text-field
                      v-model="item.title"
                      slot-scope="{
                        errors,
                      }"
                      :label="item.language.comment + ' 타이틀'"
                      class="ma-auto"
                      :readonly="!editMode"
                      :class="!editMode ? 'disable-events' : ''"
                      :error-messages="errors"
                      @focusout="crossValidation(item)"
                    />
                  </validation-provider>
                </v-col>
                <v-col cols="12">
                  <validation-provider
                    :ref="'link' + index"
                    :name="'link' + index"
                    :rules="item.rules"
                  >
                    <v-text-field
                      v-model="item.link"
                      slot-scope="{
                        errors,
                      }"
                      :label="item.language.comment + ' 링크'"
                      class="ma-auto"
                      :readonly="!editMode"
                      :class="!editMode ? 'disable-events' : ''"
                      :error-messages="errors"
                      @focusout="crossValidation(item)"
                    />
                  </validation-provider>
                </v-col>
              </v-row>
            </template>
          </v-data-table>
        </v-col>
      </v-row>
    </validation-observer>
    <v-divider class="mt-3 mb-10" />
    <button-handler
      cancel-destination="Banner"
      :edit-mode="editMode"
      :target-index="targetIndex"
      :modify-disable="editable"
      @save="save"
      @update="update(true)"
      @modify="toModify"
      @cancelModify="cancelModify"
    />
  </v-container>
</template>
<script>
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import router, { routeName } from '@/router'
  import FileUpload from '@/components/common/FileUpload'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import tagFormatMixin from '@/mixins/tagFormatMixin'
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'
  import rulesMixin from '@/mixins/rulesMixin'
  import fileUploadMixin from '@/mixins/fileUploadMixin'
  import commonFormMixin from '@/mixins/commonFormMixin'
  import store from '@/store'
  import ButtonHandler from '@/components/common/ButtonHandler'
  import { errorMsg, role, ruleType, s3PathConst, uploadType } from '@/assets/enums'
  import s3GetUrlMixin from '@/mixins/s3GetUrlMixin'
  import {
    ValidationObserver,
    ValidationProvider,
  } from 'vee-validate'

  const constant = Object.freeze({
    slotSuffix: {
      THUMBNAIL: ' 썸네일',
    },
  })

  export default {
    name: 'BannerForm',
    components: {
      FileUpload,
      CommonErrorAlert,
      ButtonHandler,
      ValidationObserver,
      ValidationProvider,
    },
    mixins: [
      commonErrorMixin,
      tagFormatMixin,
      calcOffsetMixin,
      rulesMixin,
      fileUploadMixin,
      commonFormMixin,
      s3GetUrlMixin,
    ],
    data: () => ({
      artists: store.state.common.artists,
      selectedArtist: null,
      tags: [],
      editMode: false,
      isDetail: false,
      displayFlag: false,
      localeCodes: store.state.common.localeCodes,
      formValid: true,
      uploadedFile: {},
      targetItem: JSON.parse(JSON.stringify(store.state.common.targetItem)),
      uploadedFiles: [],
      tableHeader: [
        {
          text: '언어',
          value: 'language',
          align: 'center',
          width: 100,
          sortable: false,
        },
        {
          text: '컨텐츠',
          value: 'contents',
          align: 'center',
          width: 100,
          sortable: false,
        },
        {
          text: '입력란',
          value: 'input',
          align: 'start',
          sortable: false,
        },
      ],
    }),
    computed: {
      editable () {
        const currentRole = store.state.auth.currentRole
        const creatableRoles = [role.CONTENTS_EDITOR]
        // false to disable
        return !creatableRoles.includes(currentRole)
      },
      tableData () {
        const datas = []
        const localeCodes = store.state.common.localeCodes.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
        for (const lc of localeCodes) {
          const code = lc.code
          let key = ''
          let link = ''
          let title = ''
          // display 만 들어있는 경우까지 잡기 위해 length > 1
          if (Object.keys(this.targetItem).length > 1) {
            if (this.targetItem.contents[code] !== undefined) {
              key = this.targetItem.contents[code][uploadType.IMAGE]
              link = this.targetItem.contents[code][uploadType.LINK]
            }
            if (this.targetItem.title.values !== undefined) {
              title = this.targetItem.title.values[code]
            }
          }
          const content = {
            refname: code,
            model: null,
            accept: this.imageAccept,
            slotText: '',
            url: this.getSignedUrl(key, false),
            key: key,
            isValid: true,
            rules: code === 'en' ? [ruleType.REQUIRED, ruleType.IMAGE] : [ruleType.IMAGE],
            dimension: {
              x: 0,
              y: 0,
            },
          }

          const rules = []
          if (this.isEnglish(code)) {
            rules.push('required')
          }
          const obj = {
            language: lc,
            contents: content,
            title: title,
            link: link,
            rules: rules.join('|'),
          }
          datas.push(obj)
        }
        return datas
      },
    },
    async beforeRouteEnter (to, from, next) {
      async function getArtists () {
        const params = {
          offset: 0,
          limit: -1,
        }
        await axios.get(ApiList.artists, { params }).then(res => {
          store.commit('setArtists', res.data.data)
        }).catch(() => {
          next(from.path)
        })
      }
      async function getLocaleCodes () {
        await axios.get(ApiList.countries).then(res => {
          const localeCodes = res.data.data.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
          store.commit('setLocaleCodes', localeCodes)
        }).catch(() => {
          next(from.path)
        })
      }
      await getArtists()
      await getLocaleCodes()
      next()
    },
    watch: {
      async selectedArtist (newVal, oldVal) {
        function findAndReplace (tags, val1, val2) {
          const idx = tags.findIndex(t => t === val1.name)
          if (idx >= 0) {
            tags.splice(idx, 1, val2.name)
          }
        }
        function findAndRemove (tags, val) {
          const idx = tags.findIndex(t => t === val.name)
          if (idx >= 0) {
            tags.splice(idx, 1)
          }
        }
        function insertIfNotExist (tags, val) {
          const idx = tags.findIndex(t => t === val.name)
          if (idx < 0) {
            tags.splice(0, 0, val.name)
          }
        }
        const tags = this.tags
        if (newVal === null) {
          // 삭제
          if (oldVal !== null) {
            findAndRemove(tags, oldVal)
          }
        } else {
          // 첫 선택
          if (oldVal === null) {
            insertIfNotExist(tags, newVal)
          } else {
            // 선택 변경
            findAndReplace(tags, oldVal, newVal)
          }
        }
      },
    },
    created () {
      this.init()
    },
    methods: {
      imageDropAction (lcCode) {
        const item = this.tableData.find(td => td.language.code === lcCode)
        this.crossValidation(item)
      },
      crossValidation (item) {
        const link = item.link
        const title = item.title
        const imageKey = item.contents.key
        if (link !== '' || title !== '' || imageKey !== '') {
          item.rules = 'required'
          if (!item.contents.rules.includes('required')) {
            item.contents.rules.push('required')
            item.contents.isValid = false
          }
        }
        if (link === '' && title === '' && (imageKey === '' || imageKey === null)) {
          item.rules = ''
          const idx = item.contents.rules.findIndex(r => r === 'required')
          item.contents.rules.splice(idx, 1)
          item.contents.isValid = true
        }
        if (link !== '' && title !== '' && imageKey !== '') {
          item.contents.isValid = true
        }
      },
      handleValidateRules (item) {
        const rules = []
        if (this.isEnglish(item.language.code)) {
          rules.push('required')
        }
        return rules.join('|')
      },
      isEnglish (languageCode) {
        return languageCode === 'en'
      },
      rowClass (item) {
        return 'no-hover'
      },
      displayHandler () {
        if (this.displayFlag === true) {
          return '공개'
        } else {
          return '비공개'
        }
      },
      async init () {
        this.setUploadFile()
        await this.setInitItems()
      },
      saveHandler () {
        if (this.targetItem.id === undefined) {
          this.save()
        } else {
          this.update(true)
        }
      },
      async setInitItems () {
        if (this.targetIndex !== -1) {
          this.selectedArtist = this.artists.find(a => a.id === this.defaultItem.artistId)
          this.tags = this.defaultItem.tags
          this.displayFlag = this.defaultItem.display
        }
        this.editMode = this.targetIndex === -1
        this.isDetail = this.targetIndex !== -1
      },
      async toModify () {
        this.editMode = true
        this.defaultUploadedFiles = JSON.parse(JSON.stringify(this.uploadedFiles))
      },
      async cancelModify () {
        if (this.isDetail) {
          await this.setInitItems()
          await this.setUploadFile()
          this.targetItem = JSON.parse(JSON.stringify(this.defaultItem))
          this.uploadedFiles = JSON.parse(JSON.stringify(this.defaultUploadedFiles))
          this.editMode = false
        } else {
          await router.push({ name: routeName.BANNER })
        }
      },
      async checkForm () {
        return await this.$refs.obs.validate()
      },
      async validation () {
        this.resetError()
        const con1 = await this.checkForm()
        this.uploadedFiles = [this.uploadedFile]
        for await (const td of this.tableData) {
          this.uploadedFiles.push(td.contents)
        }

        const con2 = this.checkFilesValid(this.uploadedFiles)
        return con1 && con2
      },
      async saveLanguagePacks (target) {
        // values: {
        //   en: '',
        //   ko: '',
        //   jp: '',
        // }
        const request = {
          id: target.id,
          values: target.values,
        }
        if (target.id === null || target.id === undefined) {
          // insert
          return await axios.post(ApiList.languagePacks, request).then(res => {
            return res.data.data.id
          }).catch(err => {
            this.setError(err)
          })
        } else {
          // update
          return await axios.patch(ApiList.languageWithId(target.id), request).then(res => {
            return res.data.data.id
          }).catch(err => {
            this.setError(err)
          })
        }
      },
      async setRequest () {
        const contents = {}
        const values = {}
        for (const item of this.tableData) {
          contents[item.language.code] = {}
          contents[item.language.code][uploadType.IMAGE] = item.contents.key === null ? '' : item.contents.key
          contents[item.language.code][uploadType.LINK] = item.link
          values[item.language.code] = item.title
        }
        const languagePack = {
          id: this.targetItem.title === undefined ? undefined : this.targetItem.title.id,
          values: values,
        }
        const title = await this.saveLanguagePacks(languagePack)

        return {
          tags: this.tags,
          title: title,
          artistId: this.selectedArtist.id,
          thumbnailKey: this.uploadedFile.key,
          contents: contents,
          sortOrder: this.targetItem.sortOrder === undefined ? 0 : this.targetItem.sortOrder,
          display: this.displayFlag,
        }
      },
      getUploadPath () {
        const pathArr = []
        pathArr.push(s3PathConst.ARTISTS)
        pathArr.push(this.selectedArtist.id)
        pathArr.push(s3PathConst.BANNERS)
        pathArr.push(this.targetItem.id)
        return pathArr.join(s3PathConst.DELIMITER)
      },
      async save () {
        await store.dispatch('setLoadingTrue')
        if (await this.validation() === false) {
          await store.dispatch('setLoadingFalse')
          return
        }
        const request = {}
        await axios.post(ApiList.banners, request).then(res => {
          this.targetItem.id = res.data.data.id
          this.update(false)
        }).catch(err => {
          this.setError(err)
        })
      },
      async update (doValidation) {
        if (doValidation) {
          await store.dispatch('setLoadingTrue')
          if (await this.validation() === false) {
            await store.dispatch('setLoadingFalse')
            return
          }
        }
        if (!await this.uploadFiles(this.getUploadPath(), false)) {
          this.setError(errorMsg.ERROR_WHILE_UPLOADING)
          return
        }
        const request = await this.setRequest()
        await axios.patch(ApiList.bannerWithId(this.targetItem.id), request).then(async res => {
          await this.setDefaultItem(res.data.data)
          this.editMode = false
          await store.dispatch('setLoadingFalse')
        }).catch((err) => {
          this.setError(err)
        })
      },
      async setDefaultItem (item) {
        this.defaultItem = item
        this.targetItem = JSON.parse(JSON.stringify(this.defaultItem))
      },
      async getAlbums () {
        const params = this.getParamsForAll()
        axios.get(ApiList.albums, { params }).then(async res => {
          this.albums = res.data.data
          if (this.targetIndex >= 0) {
            this.selectedAlbum = res.data.data.find(al => al.id === this.defaultItem.album.id)
          }
        }).catch((err) => {
          this.setError(err)
        })
      },
      setUploadFile () {
        let key = ''
        if (Object.keys(this.targetItem).length > 0) {
          if (this.targetItem.thumbnailKey !== undefined) {
            key = this.targetItem.thumbnailKey
          }
        }
        this.uploadedFile = {
          refname: 'thumbnail',
          model: null,
          accept: this.imageAccept,
          slotText: constant.slotSuffix.THUMBNAIL,
          url: this.getSignedUrl(key, false),
          key: key,
          isValid: true,
          rules: [ruleType.REQUIRED, ruleType.IMAGE, 'crossFieldCheck'],
          dimension: {
            x: 0,
            y: 0,
          },
        }
      },
    },
  }
</script>
<style>
.no-hover {
  background-color: transparent !important;
}
.bg-color-inherit {
  background-color: inherit !important;
}
</style>
