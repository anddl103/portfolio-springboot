<template>
  <v-card
    class="ma-auto"
  >
    <v-row class="ma-auto">
      <v-col
        cols="2"
      >
        앨범 정보
      </v-col>
      <v-spacer />
      <v-btn
        v-if="targetIndex > -1"
        class="ma-1"
        :class="{'disable-events': handleReviewButtonDisable()}"
        color="red"
        @click="handleReviewButton"
      >
        {{ reviewButtonText }}
      </v-btn>
      <v-btn
        v-if="rejectable"
        class="ma-1"
        color="secondary"
        @click="handleRejectButton"
      >
        {{ '반려' }}
      </v-btn>
      <v-btn
        v-if="cancelable"
        class="ma-1"
        color="secondary"
        @click="handleCancelButton"
      >
        {{ '승인 취소' }}
      </v-btn>
      <v-btn
        v-if="reworkable"
        class="ma-1"
        color="secondary"
        @click="handleReworkButton"
      >
        {{ '재작업' }}
      </v-btn>
    </v-row>
    <v-row
      class="ma-auto"
      justify="center"
      align="center"
    >
      <validation-observer
        ref="obs"
        class="ma-auto"
      >
        <v-row
          class="ma-auto"
          :class="album.id === undefined ? '' : 'bg-color'"
        >
          <v-col cols="12">
            <validation-provider
              name="artist"
              :rules="'required'"
            >
              <v-autocomplete
                v-model="selectedArtist"
                slot-scope="{
                  errors,
                }"
                label="아티스트"
                item-text="text"
                item-value="id"
                return-object
                required
                :items="artists"
                :readonly="targetIndex > -1"
                :append-icon="targetIndex > -1 ? '' : undefined"
                :class="targetIndex > -1 ? 'disable-events' : ''"
                :error-messages="errors"
              >
                <template v-slot:item="{ item }">
                  <v-avatar
                    left
                    class="my-3"
                  >
                    <v-img
                      :src="getSignedUrl(item.thumbnailKey)"
                      max-width="70"
                      max-height="70"
                    />
                  </v-avatar>
                  <v-divider
                    class="mx-4"
                    inset
                    vertical
                  />
                  {{ item.name }}
                </template>
                <template v-slot:selection="{ item }">
                  <v-avatar
                    left
                    class="my-3"
                  >
                    <v-img
                      :src="getSignedUrl(item.thumbnailKey)"
                      max-width="70"
                      max-height="70"
                    />
                  </v-avatar>
                  <v-divider
                    class="mx-4"
                    inset
                    vertical
                  />
                  {{ item.name }}
                </template>
              </v-autocomplete>
            </validation-provider>
          </v-col>
        </v-row>
        <v-row
          v-if="album.version !== undefined"
          class="ma-auto"
          :class="album.id === undefined ? '' : 'bg-color'"
        >
          <v-col class="mx-2">
            VERSION : {{ album.version }}
          </v-col>
        </v-row>
        <v-row
          class="ma-auto"
          :class="album.id === undefined ? '' : 'bg-color'"
        >
          <v-col
            cols="12"
          >
            <v-data-table
              :headers="languageTitleDescTableHeader"
              :items="tableData"
              hide-default-footer
              disable-sort
            >
              <template v-slot:item.language="{ item }">
                <v-text-field
                  v-model="item.language.comment"
                  class="ma-auto"
                  readonly
                />
              </template>
              <template v-slot:item.albumTitle="{ item }">
                <validation-provider
                  ref="titleProvider"
                  :rules="isEnglish(item.language.code) ? 'required' : ''"
                >
                  <v-text-field
                    v-model="targetItem.title.values[item.language.code]"
                    slot-scope="{
                      errors,
                    }"
                    class="ma-auto"
                    :readonly="!editMode"
                    :error-messages="errors"
                    @focusin="rememberOldVal(targetItem.title.values[item.language.code])"
                    @focusout="tagSyncInput(targetItem.title.values[item.language.code])"
                  />
                </validation-provider>
              </template>
              <template v-slot:item.albumDesc="{ item }">
                <validation-provider
                  ref="descProvider"
                  :rules="isEnglish(item.language.code) ? 'required' : ''"
                >
                  <v-text-field
                    v-model="targetItem.description.values[item.language.code]"
                    slot-scope="{
                      errors,
                    }"
                    class="ma-auto"
                    :readonly="!editMode"
                    :error-messages="errors"
                  />
                </validation-provider>
              </template>
              <template v-slot:item.rewardTitle="{ item }">
                <validation-provider
                  ref="rewardTitleProvider"
                  :rules="isEnglish(item.language.code) ? 'required' : ''"
                >
                  <v-text-field
                    v-model="targetItem.reward.title.values[item.language.code]"
                    slot-scope="{
                      errors,
                    }"
                    class="ma-auto"
                    :readonly="!editMode"
                    :error-messages="errors"
                  />
                </validation-provider>
              </template>
              <template v-slot:item.rewardDesc="{ item }">
                <validation-provider
                  ref="rewardDescProvider"
                  :rules="isEnglish(item.language.code) ? 'required' : ''"
                >
                  <v-text-field
                    v-model="targetItem.reward.description.values[item.language.code]"
                    slot-scope="{
                      errors,
                    }"
                    class="ma-auto"
                    :readonly="!editMode"
                    :error-messages="errors"
                  />
                </validation-provider>
              </template>
            </v-data-table>
          </v-col>
        </v-row>
        <v-row
          class="ma-auto"
          :class="album.id === undefined ? '' : 'bg-color'"
        >
          <v-col cols="12">
            <validation-provider
              ref="contentsTypeProvider"
              :rules="'required'"
            >
              <v-autocomplete
                v-model="selectedContentsType"
                slot-scope="{
                  errors,
                }"
                label="컨텐츠 타입"
                :items="contentsTypes"
                item-text="description"
                item-value="code"
                return-object
                :readonly="!editMode"
                :class="{'disable-events': !editMode}"
                :append-icon="editMode ? undefined : ''"
                :error-messages="errors"
              />
            </validation-provider>
          </v-col>
        </v-row>
        <v-row
          class="ma-auto"
          :class="album.id === undefined ? '' : 'bg-color'"
        >
          <v-col cols="12">
            <validation-provider
              ref="tagsProvider"
              :rules="'required'"
            >
              <v-combobox
                v-model="tags"
                slot-scope="{
                  errors,
                }"
                class="tag-input"
                label="검색용 태그"
                multiple
                chips
                deletable-chips
                :readonly="!editMode"
                :append-icon="editMode ? undefined : ''"
                :error-messages="errors"
              />
            </validation-provider>
          </v-col>
        </v-row>
      </validation-observer>
    </v-row>
    <v-row
      class="ma-auto"
      :class="album.id === undefined ? '' : 'bg-color'"
    >
      <v-row
        class="ma-auto"
        justify="center"
        align="center"
      >
        <file-upload
          v-for="obj in uploadedFiles.filter(uf => uf.reward === undefined)"
          :ref="obj.refname"
          :key="obj.refname"
          class="ma-3"
          :file-info="obj"
          :width="obj.isVideo ? 300 : 150"
          :height="150"
          :readonly="!editMode"
          :valid="obj.isValid"
        />
        <v-divider
          vertical
          class="ma-3"
        />
        <file-upload
          v-for="obj in uploadedFiles.filter(uf => uf.reward)"
          :ref="obj.refname"
          :key="obj.refname"
          class="ma-3"
          :file-info="obj"
          :width="obj.isVideo ? 300 : 150"
          :height="150"
          :readonly="!editMode"
          :valid="obj.isValid"
        />
      </v-row>
    </v-row>
    <button-handler
      v-if="saveBtnHandler()"
      cancel-destination="Album"
      :edit-mode="editMode"
      :target-index="targetIndex"
      @save="beforeSave"
      @update="update"
      @modify="toModify"
      @cancelModify="cancelModify"
    />
    <v-row
      v-else
      class="ma-auto"
      align-content="center"
      justify="center"
    >
      <v-btn
        class="mb-3"
        to="/Album"
      >
        {{ '뒤로' }}
      </v-btn>
    </v-row>
    <common-confirm-dialog
      :dialog-handler.sync="confirmDialog"
      :dialog-content="confirmDialogContent"
      @confirm="confirmSubmit"
    />
    <common-confirm-dialog
      :dialog-handler.sync="cancelDialog"
      :dialog-content="cancelDialogContent"
      @confirm="handleCancelDialogSubmitBtn"
    />
    <common-confirm-dialog
      :dialog-handler.sync="reworkDialog"
      :dialog-content="reworkDialogContent"
      @confirm="handleReworkDialogSubmitBtn"
    />
    <v-dialog v-model="rejectDialog">
      <v-card>
        <v-card-title
          class="text-h5"
        >
          반려 사유
        </v-card-title>
        <v-card-text>
          <v-textarea
            v-model="rejectComment"
            class="ma-auto"
            :readonly="isEditor"
            :autofocus="isManager"
            rows="10"
            outlined
            label="반려 사유"
            color="white"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="blue darken-1"
            text
            @click="rejectDialog = false"
          >
            취소
          </v-btn>
          <v-btn
            color="blue darken-1"
            text
            @click="handleRejectDialogSubmitBtn"
          >
            {{ handleRejectConfirmBtnText() }}
          </v-btn>
          <v-spacer />
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>
<script>
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import store from '@/store'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import commonAddLanguageTableMixin from '@/mixins/commonAddLanguageTableMixin'
  import tagFormatMixin from '@/mixins/tagFormatMixin'
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'
  import rulesMixin from '@/mixins/rulesMixin'
  import commonFormMixin from '@/mixins/commonFormMixin'
  import commonConfirmMixin from '@/mixins/commonConfirmMixin'
  import FileUpload from '@/components/common/FileUpload'
  import fileUploadMixin from '@/mixins/fileUploadMixin'
  import ButtonHandler from '@/components/common/ButtonHandler'
  import CommonConfirmDialog from '@/components/common/CommonConfirmDialog'
  import {
    ruleType,
    uploadType,
    s3PathConst,
    errorMsg,
    common, albumState,
  } from '@/assets/enums'
  import albumRoleMixin from '@/mixins/albumRoleMixin'
  import s3GetUrlMixin from '@/mixins/s3GetUrlMixin'
  import albumMultiLanguageTableMixin from '@/mixins/albumMultiLanguageTableMixin'
  import {
    ValidationObserver,
    ValidationProvider,
  } from 'vee-validate'
  const constant = Object.freeze({
    eventName: {
      SYNC_ARTIST: 'syncArtist',
      RESET_ERROR: 'resetError',
      ERROR: 'error',
    },
    tableId: {
      HEADER: {
        id: 'header',
        text: '',
      },
      TITLE: {
        id: 'title',
        text: '앨범 타이틀',
      },
      DESCRIPTION: {
        id: 'description',
        text: '앨범 설명',
      },
      REWARD_TITLE: {
        id: 'rewardTitle',
        text: '리워드 타이틀',
      },
      REWARD_DESCRIPTION: {
        id: 'rewardTitle',
        text: '리워드 타이틀',
      },
    },
    slotText: {
      ALBUM_THUMBNAIL: '앨범 썸네일',
      REWARD_THUMBNAIL: '리워드 썸네일',
      REWARD_IMAGE: '리워드 이미지',
      REWARD_VIDEO: '리워드 비디오',
    },
    stateBtnText: {
      editor: {
        1: '제출',
        2: '제출 취소',
        3: '반려 사유 확인',
        4: '리뷰중',
        5: '승인됨',
        6: '배포중',
        7: '배포됨',
      },
      manager: {
        1: '작업중',
        2: '리뷰 시작',
        3: '재수정 대기',
        4: '승인',
        5: '승인됨',
        6: '배포중',
        7: '배포됨',
      },
    },
  })

  export default {
    name: 'AlbumForm',
    components: {
      FileUpload,
      ButtonHandler,
      CommonConfirmDialog,
      ValidationObserver,
      ValidationProvider,
    },
    mixins: [
      commonErrorMixin,
      commonAddLanguageTableMixin,
      tagFormatMixin,
      calcOffsetMixin,
      rulesMixin,
      commonFormMixin,
      commonConfirmMixin,
      fileUploadMixin,
      albumRoleMixin,
      s3GetUrlMixin,
      albumMultiLanguageTableMixin,
    ],
    props: {
      album: Object,
      isWorkingAlbum: Boolean,
    },
    data: () => ({
      errors: {
        artist: {
          error: false,
          errorMsg: '',
        },
        title: {
          error: false,
          errorMsg: '',
        },
      },
      tables: [],
      contentsType: [],
      uploadedFiles: [],
      defaultUploadedFiles: [],
      artists: [],
      editMode: false,
      names: null,
      allowModify: false,
      targetItem: {},
      tags: [],
      formValid: true,
      selectedArtist: null,
      rejectComment: '',
      rejectDialog: false,
      cancelDialog: false,
      cancelDialogContent: '승인을 취소하시겠습니까? <br>\'제출됨\' 상태로 변경됩니다.',
      reworkDialog: false,
      reworkDialogContent: '배포된 카드를 수정하시겠습니까? <br>\'작업중\' 상태로 변경됩니다.',
      oldVal: '',
      contentsTypes: store.state.common.contentsTypes,
      selectedContentsType: '',
    }),
    computed: {
      reviewButtonText () {
        if (this.album.state === undefined) return 'ERROR'
        if (this.isEditor) {
          return constant.stateBtnText.editor[this.album.state.code]
        }
        if (this.isManager) {
          return constant.stateBtnText.manager[this.album.state.code]
        }
        return 'NO PERMISSIONS'
      },
      rejectable () {
        return this.isManager && this.reviewButtonText === constant.stateBtnText.manager[4]
      },
      cancelable () {
        return this.isManager && this.reviewButtonText === constant.stateBtnText.manager[5]
      },
      reworkable () {
        return this.isEditor && this.reviewButtonText === constant.stateBtnText.editor[7]
      },
    },
    watch: {
      selectedArtist (newItem, oldItem) {
        if (newItem === null && oldItem !== null) {
          // 삭제함
          const idx = this.tags.findIndex(t => t === oldItem.name)
          this.tags.splice(idx, 1)
          this.cards = []
          this.selectedCards = []
        }
        if (newItem !== null && oldItem === null) {
          // 추가됨
          const idx = this.tags.findIndex(t => t === newItem.name)
          if (idx < 0) {
            this.tags.splice(0, 0, newItem.name)
          }
          this.$emit(constant.eventName.SYNC_ARTIST, newItem.id)
        }
        if (newItem !== null && oldItem !== null) {
          const idx = this.tags.findIndex(t => t === oldItem.name)
          if (idx < 0) {
            this.tags.splice(0, 0, newItem.name)
          } else {
            this.tags.splice(idx, 1, newItem.name)
          }
          this.$emit(constant.eventName.SYNC_ARTIST, newItem.id)
        }
      },
    },
    created () {
      this.initialize()
    },
    methods: {
      isEnglish (languageCode) {
        return languageCode === 'en'
      },
      async handleCancelDialogSubmitBtn () {
        await this.setAlbumState(albumState.SUBMITTED)
      },
      async handleReworkDialogSubmitBtn () {
        await this.setAlbumState(albumState.WORKING)
      },
      saveBtnHandler () {
        if (this.isEditor) {
          // 신규
          if (this.targetIndex < 0) return true
          // 작업중
          if (this.isState(albumState.WORKING)) return true
        }
        return false
      },
      handleReviewButtonDisable () {
        // true : disable

        // common
        if (this.isState(albumState.DEPLOYED)) return true

        if (this.isEditor) {
          if (this.isState(albumState.REVIEWING)) return true
          if (this.isState(albumState.CONFIRMED)) return true
        }

        if (this.isManager) {
          if (this.isState(albumState.WORKING)) return true
          if (this.isState(albumState.REJECTED)) return true
          if (this.isState(albumState.CONFIRMED)) return true
        }
        return false
      },
      handleRejectConfirmBtnText () {
        if (this.isManager) return '확인'
        if (this.isEditor) return '회수'
      },
      async handleRejectDialogSubmitBtn () {
        if (this.isManager) {
          await this.rejectAlbum()
          this.rejectDialog = false
        }
        if (this.isEditor) {
          await this.setAlbumState(albumState.WORKING)
          this.rejectDialog = false
        }
      },
      async rejectAlbum () {
        const req = {
          comment: this.rejectComment,
        }
        await this.setAlbumStateWithParams(albumState.REJECTED, req)
      },
      handleReworkButton () {
        this.reworkDialog = !this.reworkDialog
      },
      handleCancelButton () {
        this.cancelDialog = !this.cancelDialog
      },
      async handleRejectButton () {
        this.rejectDialog = !this.rejectDialog
      },
      async handleReviewButton () {
        if (this.reviewButtonText === constant.stateBtnText.editor[3]) {
          this.rejectDialog = !this.rejectDialog
          return
        }
        let newState = albumState.INVALID

        // 제출
        if (this.reviewButtonText === constant.stateBtnText.editor[1]) {
          newState = albumState.SUBMITTED
        }
        // 제출 취소
        if (this.reviewButtonText === constant.stateBtnText.editor[2]) {
          newState = albumState.WORKING
        }
        // 리뷰 시작
        if (this.reviewButtonText === constant.stateBtnText.manager[2]) {
          newState = albumState.REVIEWING
        }
        // 승인
        if (this.reviewButtonText === constant.stateBtnText.manager[4]) {
          newState = albumState.CONFIRMED
        }
        if (newState === albumState.INVALID) {
          return
        }
        await this.setAlbumState(newState)
      },
      setError (err) {
        if (err === common.EMPTY) {
          this.$emit(constant.eventName.RESET_ERROR)
        } else {
          this.$emit(constant.eventName.ERROR, err)
        }
      },
      async setAlbumState (state) {
        await store.dispatch('setLoadingTrue')
        const stateKey = Object.entries(albumState).find((k, v) => v === state)[0]
        const url = ApiList.albumSetState(this.album.id, stateKey.toLowerCase())
        await axios.patch(url).then(async res => {
          this.defaultItem = res.data.data
          await store.commit('setTargetItem', res.data.data)
          this.$emit('update:album', res.data.data)
          await store.dispatch('setLoadingFalse')
        }).catch(err => {
          this.setError(err)
          store.dispatch('setLoadingFalse')
        })
      },
      async setAlbumStateWithParams (state, req) {
        await store.dispatch('setLoadingTrue')
        const stateKey = Object.entries(albumState).find((k, v) => v === state)[0]
        const url = ApiList.albumSetState(this.album.id, stateKey.toLowerCase())
        axios.patch(url, req).then(async res => {
          this.defaultItem = res.data.data
          await store.commit('setTargetItem', res.data.data)
          this.rejectDialog = false
          this.$emit('update:album', res.data.data)
          await store.dispatch('setLoadingFalse')
        }).catch(err => {
          this.setError(err)
          store.dispatch('setLoadingFalse')
        })
      },
      async saveLanguagePacks (target) {
        const request = {
          values: target.values,
        }
        if (target.id === null) {
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
      async setRequest (firstTime) {
        const title = await this.saveLanguagePacks(this.targetItem.title)
        const description = await this.saveLanguagePacks(this.targetItem.description)

        let res = {
          artistId: this.selectedArtist.id,
          title: title,
          description: description,
        }
        if (firstTime === undefined) {
          const thumbnailKey = this.uploadedFiles.find(uf => uf.refname === uploadType.THUMBNAIL).key
          const rewardTitle = await this.saveLanguagePacks(this.targetItem.reward.title)
          const rewardDescription = await this.saveLanguagePacks(this.targetItem.reward.description)
          const rewardThumbnailKey = this.uploadedFiles.find(uf => uf.refname === uploadType.REWARD_THUMBNAIL).key
          const rewardImageKey = this.uploadedFiles.find(uf => uf.refname === uploadType.REWARD_IMAGE).key
          const rewardVideoKey = this.uploadedFiles.find(uf => uf.refname === uploadType.REWARD_VIDEO).key
          const reward = {
            title: rewardTitle,
            description: rewardDescription,
            thumbnailKey: rewardThumbnailKey,
            type: this.selectedContentsType.code,
            imageKey: rewardImageKey,
            videoKey: rewardVideoKey,
          }
          res = {
            tags: this.tags,
            artistId: this.selectedArtist.id,
            title: title,
            description: description,
            thumbnailKey: thumbnailKey,
            reward: reward,
          }
        }
        return res
      },
      async cancelModify () {
        await this.setTargetItem()
        this.uploadedFiles = JSON.parse(JSON.stringify(this.defaultUploadedFiles))
        await this.setInitArtist()
        this.editMode = false
      },
      async update () {
        const req = await this.beforeTransition()

        await axios.patch(ApiList.workingAlbumWithId(this.album.id), req).then(async res => {
          if (!await this.validateResponse(res)) return
          this.defaultItem = res.data.data

          await store.commit('setTargetItem', res.data.data)
          await store.commit('setTargetAlbumId', res.data.data.id)
          this.$emit('update:album', res.data.data)
          this.editMode = false
        }).catch(err => {
          this.setError(err)
        })
      },
      async toModify () {
        this.editMode = true
        this.defaultUploadedFiles = JSON.parse(JSON.stringify(this.uploadedFiles))
      },
      async beforeTransition () {
        this.setError(common.EMPTY)
        await store.dispatch('setLoadingTrue')
        if (!await this.checkAlbumForm()) {
          this.setError(errorMsg.CHECK_INPUT)
          return false
        }
        if (!await this.uploadFiles(this.getUploadPath(), true)) {
          this.setError(errorMsg.ERROR_WHILE_UPLOADING)
          return false
        }

        return await this.setRequest()
          .then(res => { return res })
          .catch(err => this.setError(err))
      },
      getUploadPath () {
        const pathArr = []
        pathArr.push(s3PathConst.ARTISTS)
        pathArr.push(this.selectedArtist.id)
        pathArr.push(s3PathConst.ALBUMS)
        pathArr.push(this.album.id)
        return pathArr.join(s3PathConst.DELIMITER)
      },
      async beforeSave () {
        this.setError(common.EMPTY)
        if (!await this.checkAlbumForm()) {
          this.setError(errorMsg.CHECK_INPUT)
          return false
        }
        const warnStr = '아티스트는 저장 후 수정이 불가능합니다.<br /> <br /> 정말 저장 하시겠습니까?'
        this.openConfirmDialog(warnStr)
      },
      async save () {
        await store.dispatch('setLoadingTrue')
        const req = await this.setRequest(true)
          .then(res => { return res })
          .catch(err => this.setError(err))

        axios.post(ApiList.workingAlbums, req).then(async res => {
          if (!await this.validateResponse(res)) return
          await this.$emit('update:album', res.data.data)
          // index >= 0 일 때 저장버튼이 업데이트를 실행하도록 되어있기 때문에 임의로 인덱스를 높여준다.
          this.targetIndex = 999
          await this.update()
          this.editMode = false
        }).catch(err => {
          this.setError(err)
        })
      },
      rememberOldVal (val) {
        if (val === undefined) {
          this.oldVal = ''
        } else {
          this.oldVal = val
        }
      },
      contentsSyncInput (val, target) {
        target = val
      },
      tagSyncInput (newVal) {
        const oldVal = this.oldVal
        if (newVal === undefined) newVal = ''
        // 삭제
        if (newVal === '') {
          // 지운 값과 같은 값이 사용되지 않고 있으면 태그에서 제거
          if (this.tableDataLanguageData.filter(str => str === oldVal).length <= 0) {
            const idx = this.tags.findIndex(t => t === oldVal)
            if (idx >= 0) this.tags.splice(idx, 1)
          }
        }

        if (newVal !== '') {
          if (this.tableDataLanguageData.filter(str => str === oldVal).length <= 0) {
            const idx = this.tags.findIndex(t => t === oldVal)
            if (idx >= 0) this.tags.splice(idx, 1)
          }

          if (this.tags.filter(str => str === newVal).length <= 0) {
            this.tags.push(newVal)
          }
          if (this.tags.filter(str => str === newVal).length > 1) {
            const idx = this.tags.findIndex(t => t === oldVal)
            if (idx >= 0) {
              this.tags.splice(idx, 1, newVal)
            } else {
              this.tags.push(newVal)
            }
          }
        }
      },
      async initialize () {
        this.getArtists()
        this.setTargetItem()
        await this.setUploadedFiles()
        this.decideEditMode()
        if (this.isState(albumState.REJECTED)) this.rejectComment = this.album.rejectInfo.comment
      },
      getArtists () {
        const params = this.getParamsForAll()
        axios.get(ApiList.artists, { params }).then(res => {
          this.artists = res.data.data
          if (this.targetIndex > -1) {
            this.selectedArtist = this.artists.find(a => a.id === this.targetItem.artistId)
          }
        })
      },
      initContentsType () {
        this.selectedContentsType = this.targetItem.reward.type
      },
      initTags () {
        const arr = []
        // 처음부터 targetItem.reward 가 있기 때문에 length > 1 로 확인
        if (Object.keys(this.targetItem).length > 1) {
          arr.push(...this.targetItem.tags)
        }
        this.tags = arr
      },
      setTargetItem () {
        if (this.album.id === undefined) {
          this.targetItem = {
            title: {
              id: null,
              values: {},
            },
            description: {
              id: null,
              values: {},
            },
            reward: {
              title: {
                id: null,
                values: {},
              },
              description: {
                id: null,
                values: {},
              },
            },
          }
        } else {
          this.targetItem = JSON.parse(JSON.stringify(this.album))
          this.initTags()
          this.initContentsType()
        }
      },
      decideEditMode () {
        this.editMode = this.targetIndex === -1
      },
      async checkAlbumForm () {
        // input validation
        const con1 = await this.$refs.obs.validate()
        // files validation
        const con2 = this.checkFilesValid(this.uploadedFiles)
        return con1 && con2
      },
      setInitArtist () {
        if (this.artists !== undefined && this.artists.length > 0) {
          this.selectedArtist = this.artists.find(ar => ar.id === this.targetItem.artistId)
        }
      },
      async setUploadedFiles () {
        this.uploadedFiles = [
          {
            refname: uploadType.THUMBNAIL,
            model: null,
            accept: this.imageAccept,
            slotText: constant.slotText.ALBUM_THUMBNAIL,
            key: this.targetItem.thumbnailKey === undefined ? common.EMPTY : this.targetItem.thumbnailKey,
            isKey: false,
            isVideo: false,
            isValid: true,
            rules: [ruleType.REQUIRED, ruleType.IMAGE],
            dimension: {
              x: 0,
              y: 0,
            },
            isWorkingAlbum: this.isWorkingAlbum,
          },
          {
            refname: uploadType.REWARD_THUMBNAIL,
            model: null,
            accept: this.imageAccept,
            slotText: constant.slotText.REWARD_THUMBNAIL,
            key: this.targetItem.reward === undefined ? common.EMPTY : this.targetItem.reward.thumbnailKey === undefined ? common.EMPTY : this.targetItem.reward.thumbnailKey,
            isKey: false,
            isVideo: false,
            isValid: true,
            rules: [ruleType.REQUIRED, ruleType.IMAGE],
            reward: true,
            dimension: {
              x: 0,
              y: 0,
            },
            isWorkingAlbum: this.isWorkingAlbum,
          },
          {
            refname: uploadType.REWARD_IMAGE,
            model: null,
            accept: this.imageAccept,
            slotText: constant.slotText.REWARD_IMAGE,
            isKey: true,
            key: this.targetItem.reward === undefined ? common.EMPTY : this.targetItem.reward.imageKey === undefined ? common.EMPTY : this.targetItem.reward.imageKey,
            isVideo: false,
            isValid: true,
            reward: true,
            rules: [ruleType.REQUIRED, ruleType.IMAGE],
            dimension: {
              x: 0,
              y: 0,
            },
            isWorkingAlbum: this.isWorkingAlbum,
          },
          {
            refname: uploadType.REWARD_VIDEO,
            model: null,
            accept: this.videoAccept,
            slotText: constant.slotText.REWARD_VIDEO,
            isKey: true,
            key: this.targetItem.reward === undefined ? common.EMPTY : this.targetItem.reward.videoKey === undefined ? common.EMPTY : this.targetItem.reward.videoKey,
            isVideo: true,
            isValid: true,
            reward: true,
            rules: [ruleType.REQUIRED, ruleType.VIDEO],
            dimension: {
              x: 0,
              y: 0,
            },
            isWorkingAlbum: this.isWorkingAlbum,
          },
        ]
      },
    },
  }
</script>
<style scoped>
.bg-color:nth-child(even) {
  background-color : #333333 !important
}
</style>
