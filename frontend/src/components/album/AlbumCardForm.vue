<template>
  <v-container
    class="pa-4 dialog"
  >
    <common-error-alert
      :error="error"
      :error-msg="errorMsg"
      @close="resetError"
    />

    <common-confirm-dialog
      :dialog-handler.sync="confirmDialog"
      :dialog-content="confirmDialogContent"
      @confirm="confirmSubmit"
    />
    <v-row class="ma-auto">
      <v-col>
        카드 정보
      </v-col>
    </v-row>
    <v-row class="ma-auto">
      <v-col>
        <v-form
          ref="form"
          v-model="formValid"
          lazy-validation
        >
          <v-autocomplete
            v-model="selectedMembers"
            label="멤버 필터"
            :items="members"
            :item-text="item => item.name"
            return-object
            chips
            multiple
            :rules="[required]"
            deletable-chips
            class="tag-input"
          >
            <template v-slot:item="{ item }">
              {{ handleMemberName(item) }}
            </template>
            <template v-slot:selection="{ item }">
              <v-chip>
                {{ handleMemberName(item) }}
              </v-chip>
            </template>
          </v-autocomplete>
          <v-autocomplete
            v-model="selectedType"
            label="컨텐츠 타입"
            :items="contentsTypes"
            :rules="[required]"
            item-text="description"
            item-value="code"
          />
          <v-combobox
            v-model="tags"
            label="검색용 태그"
            append-icon
            chips
            multiple
            :rules="[required]"
            deletable-chips
            class="tag-input"
          />
        </v-form>
      </v-col>
    </v-row>
    <v-divider class="my-3" />
    <v-row
      class="ma-auto"
      align="center"
      justify="center"
    >
      <file-upload
        v-for="obj in uploadedFiles"
        :ref="obj.refname"
        :key="obj.refname"
        class="ma-3"
        :file-info="obj"
        :width="obj.isVideo ? 300 : 150"
        :height="150"
        :valid="obj.isValid"
      />
    </v-row>
    <v-divider class="mt-3 mb-10" />
    <v-row
      class="ma-auto"
      justify="center"
    >
      <v-btn
        color="primary"
        @click="beforeSubmit"
      >
        임시 저장
      </v-btn>
      <v-btn
        @click="handleCancel"
      >
        취소
      </v-btn>
    </v-row>
  </v-container>
</template>
<script>
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import store from '@/store'
  import FileUpload from '@/components/common/FileUpload'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import fileUploadMixin from '@/mixins/fileUploadMixin'
  import rulesMixin from '@/mixins/rulesMixin'
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'
  import CommonConfirmDialog from '@/components/common/CommonConfirmDialog'
  import commonConfirmMixin from '@/mixins/commonConfirmMixin'
  import { contentsType, operationType, ruleType, uploadType, common } from '@/assets/enums'
  const constant = Object.freeze({
    eventName: {
      CLOSE: 'close',
      TEMP_SAVE: 'temporarySave',
    },
    slotText: {
      THUMBNAIL: '보유시 썸네일',
      IMAGE: '컨텐츠 이미지',
      VIDEO: '컨텐츠 비디오',
    },
    dialogText: {
      IMAGE_STILL: '이미지가 업로드 되어있지 않습니다. <br /> 컨텐츠 타입이 STILL 이 맞습니까?',
      IMAGE_LIVE: '이미지가 업로드 되어있지 않습니다. <br /> 컨텐츠 타입이 LIVE 가 맞습니까?',
      VIDEO_VIDEO: '비디오가 업로드 되어있지 않습니다. <br /> 컨텐츠 타입이 VIDEO 가 맞습니까?',
      VIDEO_LIVE: '비디오가 업로드 되어있지 않습니다. <br /> 컨텐츠 타입이 LIVE 가 맞습니까?',
    },
  })

  export default {
    name: 'AlbumCardForm',
    components: {
      FileUpload,
      CommonErrorAlert,
      CommonConfirmDialog,
    },
    mixins: [
      fileUploadMixin,
      commonErrorMixin,
      rulesMixin,
      calcOffsetMixin,
      commonConfirmMixin,
    ],
    props: {
      artist: Object,
      album: Object,
      members: Array,
      handler: Boolean,
    },
    data: () => ({
      tags: [],
      selectedArtist: null,
      selectedMembers: [],
      contentsTypes: store.state.common.contentsTypes,
      editMode: false,
      formValid: true,
      selectedType: '',
      uploadedFiles: [],
      targetCard: {},
      targetIndex: store.state.common.targetAlbumCardIndex,
      targetItem: store.state.common.targetAlbumCard,
    }),
    watch: {
      selectedMembers (after, before) {
        if (after.length !== before.length) {
          // 추가
          if (after.length > before.length) {
            const member = after.filter(val => !before.includes(val))[0]
            for (const memVal of Object.values(member.values)) {
              if (this.tags.find(t => t === memVal) === undefined) {
                this.tags.push(memVal)
              }
            }
          } else {
            const member = before.filter(val => !after.includes(val))[0]
            for (const name of Object.values(member.values)) {
              const idx = this.tags.findIndex(t => t === name)
              if (idx >= 0) {
                this.tags.splice(idx, 1)
              }
            }
          }
        }
      },
      handler: {
        handler () {
          if (this.handler) {
            this.targetItem = store.state.common.targetAlbumCard
            this.targetIndex = store.state.common.targetAlbumCardIndex
            this.setAll(this.targetItem)
          } else {
            store.commit('resetTargetAlbumCard')
            this.targetItem = store.state.common.targetAlbumCard
            this.targetIndex = store.state.common.targetAlbumCardIndex
            this.setAll()
          }
        },
        immediate: true,
      },
    },
    mounted () {
      this.init()
    },
    methods: {
      handleMemberName (item) {
        const values = item.values
        if (values.ko === undefined) return values.en

        return values.ko
      },
      async setAll (item) {
        if (item === undefined || item.albumId === common.EMPTY) {
          if (this.$refs.form !== undefined) {
            this.$refs.form.reset()
            this.$refs.form.resetValidation()
          }
          this.selectedType = common.EMPTY
          this.selectedMembers = []

          // default tags : artist name, album title(s)
          this.tags.push(this.artist.name)
          const albumTitle = this.album.title.values
          const titles = new Set([...Object.values(albumTitle)])
          this.tags.push(...titles)

          await this.setUploadedFiles()
        } else {
          if (this.targetItem.contents.type.code === undefined) {
            this.selectedType = this.targetItem.contents.type
          } else {
            this.selectedType = this.targetItem.contents.type.code
          }
          for (const timId of this.targetItem.members) {
            const memObj = this.members.find(m => m.id === timId)
            if (memObj !== undefined) {
              this.selectedMembers.push(memObj)
            }
          }
          // this.selectedMembers = this.targetItem.members
          if (item.uploadedFiles === undefined) {
            await this.setUploadedFiles()
          } else {
            this.uploadedFiles = JSON.parse(JSON.stringify(item.uploadedFiles))
          }
        }
      },
      async init () {
        await this.getArtists()
        await this.setInitTags()
      },
      async setInitTags () {
        this.tags = [...this.album.tags]
      },
      handleCancel () {
        this.$emit(constant.eventName.CLOSE)
        this.$refs.form.reset()
        store.commit('resetTargetAlbumCard')
        this.setUploadedFiles()
      },
      async getValidMembers () {
        const arr = []
        for await (const sm of this.selectedMembers) {
          if (this.members.findIndex(m => m.id === sm.id) >= 0) {
            arr.push(sm.id)
          }
        }
        return arr
      },
      async setContentKey (req, target) {
        const source = this.uploadedFiles.find(uf => uf.refname === target)
        if (this.editMode) {
          if (source.key === null) {
            req.contents[target] = common.EMPTY
          } else if (source.model === null) {
            req.contents[target] = this.defaultItem.contents[target]
          } else {
            req.contents[target] = source.key
          }
        } else {
          req.contents[target] = source.key
        }
      },
      async setRequest () {
        const members = await this.getValidMembers()
        const thumbnail = this.uploadedFiles.find(uf => uf.refname === uploadType.THUMBNAIL)

        const req = {
          artistId: this.selectedArtist.id,
          tags: this.tags,
          members: members,
          contents: {
            thumbnailKey: thumbnail.key,
            type: this.selectedType,
          },
        }

        await this.setContentKey(req, uploadType.IMAGE)
        await this.setContentKey(req, uploadType.VIDEO)

        return req
      },
      validation () {
        if (this.selectedType === contentsType.STILL) {
          this.uploadedFiles.find(uf => uf.refname === uploadType.VIDEO).passValidation = true
        }
        const con1 = this.checkForm()
        const con2 = this.checkAtLeastOneContent()
        const con3 = this.checkFilesValid(this.uploadedFiles)
        if (!con2) {
          this.$vuetify.goTo(0)
          this.setError(this.needAtLeastError)
          return false
        }
        return con1 && con2 && con3
      },
      checkForm () {
        return this.$refs.form.validate()
      },
      confirmContentsType () {
        const code = this.selectedType
        const image = this.uploadedFiles.find(uf => uf.refname === uploadType.IMAGE)
        const video = this.uploadedFiles.find(uf => uf.refname === uploadType.VIDEO)
        if (code === this.contentsTypes.find(t => t.code === contentsType.STILL)) {
          if (!this.checkFileExists(image)) {
            this.openConfirmDialog(constant.dialogText.IMAGE_STILL)
            return
          }
        }
        if (code === this.contentsTypes.find(t => t.code === contentsType.VIDEO)) {
          if (!this.checkFileExists(video)) {
            this.openConfirmDialog(constant.dialogText.VIDEO_VIDEO)
            return
          }
        }
        this.save()
      },
      beforeSubmit () {
        if (!this.validation()) return
        this.confirmContentsType()
      },
      async save () {
        const thumbnail = this.uploadedFiles.find(uf => uf.refname === uploadType.THUMBNAIL)
        const image = this.uploadedFiles.find(uf => uf.refname === uploadType.IMAGE)
        const video = this.uploadedFiles.find(uf => uf.refname === uploadType.VIDEO)

        if (this.targetItem.id === undefined || this.targetItem.id === common.EMPTY) {
          this.targetCard.operationType = operationType.INSERT
        } else {
          this.targetCard = JSON.parse(JSON.stringify(this.targetItem))
          this.targetCard.operationType = operationType.UPDATE
        }
        this.targetCard.contents = {
          thumbnailKey: thumbnail.key,
          imageKey: image.key,
          videoKey: video.key,
          type: this.contentsTypes.find(ct => ct.code === this.selectedType),
        }
        if (this.selectedType !== constant.dialogText.VIDEO_VIDEO) {
          video.isValid = true
        }
        this.targetCard.members = this.selectedMembers.map(sm => sm.id).sort()
        this.targetCard.uploadedFiles = this.uploadedFiles
        this.targetCard.tags = this.tags

        this.$emit(constant.eventName.TEMP_SAVE, this.targetCard)
        this.$refs.form.reset()
        this.targetCard = {}
      },
      async getArtists () {
        const params = this.getParamsForAll()
        await axios.get(ApiList.artists, { params }).then(res => {
          this.artists = res.data.data
        }).catch((err) => {
          this.setError(err)
        })
      },
      async setUploadedFiles () {
        this.uploadedFiles = [
          {
            refname: uploadType.THUMBNAIL,
            model: null,
            accept: this.imageAccept,
            slotText: constant.slotText.THUMBNAIL,
            isKey: false,
            key: this.targetItem.contents !== undefined ? this.targetItem.contents.thumbnailKey : common.EMPTY,
            isVideo: false,
            isValid: true,
            rules: [ruleType.REQUIRED, ruleType.IMAGE],
            dimension: {
              x: 0,
              y: 0,
            },
            isWorkingAlbum: true,
            passValidation: false,
          },
          {
            refname: uploadType.IMAGE,
            model: null,
            accept: this.imageAccept,
            slotText: constant.slotText.IMAGE,
            isKey: true,
            key: this.targetItem.contents !== undefined ? this.targetItem.contents.imageKey : common.EMPTY,
            isVideo: false,
            isValid: true,
            rules: [ruleType.IMAGE],
            dimension: {
              x: 0,
              y: 0,
            },
            isWorkingAlbum: true,
            passValidation: false,
          },
          {
            refname: uploadType.VIDEO,
            model: null,
            accept: this.videoAccept,
            slotText: constant.slotText.VIDEO,
            isKey: true,
            key: this.targetItem.contents !== undefined ? this.targetItem.contents.videoKey : common.EMPTY,
            isVideo: true,
            isValid: true,
            rules: [ruleType.VIDEO],
            dimension: {
              x: 0,
              y: 0,
            },
            isWorkingAlbum: true,
            passValidation: false,
          },
        ]
      },
    },
  }
</script>
<style>
  .dialog {
    background-color: #0D0D0D;
  }
</style>
