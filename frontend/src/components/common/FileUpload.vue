<template>
  <v-card
    class="height-max-content"
    flat
  >
    <v-card-title class="justify-center pa-1">
      <v-row
        class="ma-auto pa-2 width-webkit-fill"
        justify="center"
      >
        <label class="v-label">{{ fileInfo.slotText }}</label>
      </v-row>
      <v-row
        v-if="!readonly && fileInfo.key !== '' && fileInfo.key !== null"
        class="ma-auto pa-2 width-webkit-fill absolute-row"
        justify="end"
      >
        <v-icon
          class="overlap"
          @click.stop="deleteFile"
        >
          mdi-close
        </v-icon>
      </v-row>
    </v-card-title>
    <v-card-text>
      <v-responsive
        class="ma-auto"
      >
        <validation-provider
          :ref="'fileUpload_'+fileInfo.refname"
          :name="'fileUpload_'+fileInfo.refname"
          :rules="handleRules"
        >
          <input
            ref="fileInput"
            type="file"
            hidden
            :accept="fileInfo.accept"
            @change="onChange"
          >
        </validation-provider>
        <span class="display-grid">
          <v-input
            class="ma-auto"
            :messages="showFileInfo"
            :error="error"
            :error-messages="errorMsg"
          >
            <template v-slot:message="{ message }">
              <label>{{ message }}</label>
            </template>
            <v-sheet
              class="ma-auto"
              :class="handleSheetStyle"
              :width="width"
              :height="height"
              @dragover.prevent
              @dragenter.prevent
              @drop.prevent="onDrop"
              @click.stop="onClickFile"
            >
              <v-row
                v-if="fileInfo.key"
                class="ma-auto list-image"
              >
                <v-img
                  v-if="!fileInfo.isVideo"
                  v-model="fileInfo.model"
                  class="ma-auto list-image"
                  contain
                  :src="fileInfo.url"
                />
                <video-player
                  v-if="fileInfo.isVideo"
                  :id="fileInfo.refname"
                  :ref-name="fileInfo.refname"
                  class="ma-auto list-image"
                  :options="videoOptions"
                  :src="fileInfo.url"
                  :model="fileInfo.model"
                  @dimension="setVideoDimension"
                />
              </v-row>
              <v-row
                v-else
                class="ma-auto height-webkit-fill justify-center align-center"
              >
                <p v-if="readonly">
                  없음
                </p>
                <p
                  v-else
                  class="text-align-center"
                >
                  Click to choose file <br>
                  OR <br>
                  Drag & drop here
                </p>
              </v-row>
            </v-sheet>
          </v-input>
        </span>
      </v-responsive>
    </v-card-text>
  </v-card>
</template>

<script>
  import VideoPlayer from '@/components/common/VideoPlayer'
  import 'video.js/dist/video-js.css'
  import fileUploadMixin from '@/mixins/fileUploadMixin'
  import rulesMixin from '@/mixins/rulesMixin'
  import s3GetUrlMixin from '@/mixins/s3GetUrlMixin'

  export default {
    name: 'FileUpload',
    components: {
      VideoPlayer,
    },
    mixins: [
      fileUploadMixin,
      rulesMixin,
      s3GetUrlMixin,
    ],
    props: {
      fileInfo: { type: Object, default: () => {} },
      readonly: Boolean,
      valid: Boolean,
    },
    data: () => ({
      STATE_LOCAL: 'local',
      STATE_SERVER: 's3',
      videoOptions: {
        controls: true,
      },
      error: false,
      errorMsg: '',
      ratio: 0,
    }),
    computed: {
      width () {
        return 180
        // if (this.ratio === 1) {
        //   return 180
        // } else {
        //   if (this.ratio > 1) {
        //     return 200
        //   } else {
        //     return 300
        //   }
        // }
      },
      height () {
        return 180
        // if (this.ratio === 1) {
        //   return 180
        // } else {
        //   if (this.ratio > 1) {
        //     return 300
        //   } else {
        //     return 180
        //   }
        // }
      },
      showFileInfo () {
        const { x, y } = this.fileInfo.dimension
        if (x + y === 0) {
          return ''
        } else {
          return this.fileInfo.dimension.x + 'x' + this.fileInfo.dimension.y
        }
      },
      handleSheetStyle () {
        if (this.error) {
          return 'sheet-error'
        } else {
          if (this.readonly) {
            return 'sheet-read-only'
          } else {
            return 'sheet'
          }
        }
      },
      handleRules () {
        const rules = []
        let rule = ''
        if (this.fileInfo.rules) {
          if (this.fileInfo.rules.includes('required')) {
            rules.push('required')
          }
          if (this.fileInfo.rules.includes('image')) {
            rules.push('mimes:image/*')
          }
          if (this.fileInfo.rules.includes('video')) {
            rules.push('mimes:video/*')
          }
          rule = rules.join('|')
        }
        return rule
      },
    },
    watch: {
      'fileInfo.passValidation' () {
        if (this.fileInfo.passValidation === true) {
          this.setError(false, '')
        }
      },
      'fileInfo.isValid' () {
        if (this.fileInfo.key) {
          if (this.fileInfo.isValid) {
            this.setError(false, '')
          }
        } else {
          this.fileValidate(this.fileInfo.model)
        }
      },
      'fileInfo.key' () {
        this.getUrl(this.fileInfo.key, this.fileInfo.isWorkingAlbum)
        this.setVideoSource()
        this.setImageDimensions()
      },
    },
    mounted () {
      this.init()
    },
    methods: {
      async init () {
        await this.getUrl(this.fileInfo.key, this.fileInfo.isWorkingAlbum)
        await this.setVideoSource()
        this.setImageDimensions()
      },
      async setVideoSource () {
        if (this.fileInfo.isVideo) {
          const source = {
            src: this.fileInfo.url,
          }
          if (this.fileInfo.model !== null) {
            source.type = this.fileInfo.model.type
          }
          this.$set(this.videoOptions, 'sources', [source])
        }
      },
      deleteFile (e) {
        this.$refs.fileInput.value = null
        this.setFileEmpty()
        this.fileValidate(this.fileInfo.model)
        this.$emit('removeAction', this.fileInfo.refname)
      },
      async setImageSrc () {
        const file = this.fileInfo.model
        if (file !== undefined) {
          if (await this.fileValidate(file)) {
            await this.setVideoSource()
            const url = URL.createObjectURL(file)
            this.fileInfo.key = url
            this.fileInfo.url = url
            this.fileInfo.state = this.STATE_LOCAL
            this.$emit('dropAction', this.fileInfo.refname)
            this.setImageDimensions()
          } else {
            this.setFileEmpty()
          }
        }
      },
      async fileValidate (file) {
        return await this.$refs['fileUpload_' + this.fileInfo.refname].validate(file).then(res => {
          this.error = !res.valid
          this.errorMsg = res.valid ? '' : res.errors[0]
          this.fileInfo.isValid = res.valid
          return res.valid
        })
      },
      onDrop (event) {
        this.fileInfo.model = event.dataTransfer.files[0]
        this.setImageSrc()
      },
      onChange (event) {
        this.fileInfo.model = event.target.files[0]
        this.setImageSrc()
      },
      onClickFile (e) {
        if (this.readonly) {
          e.stopPropagation()
        } else {
          this.$refs.fileInput.click()
        }
      },
      setFileEmpty () {
        if (this.fileInfo.key) this.fileInfo.key = null
        if (this.fileInfo.url) this.fileInfo.url = null
        if (this.fileInfo.model) this.fileInfo.model = null
        this.fileInfo.dimension.x = 0
        this.fileInfo.dimension.y = 0
        this.setRatio()
      },
      setRatio (width, height) {
        if (width === undefined || height === undefined) {
          this.ratio = 1
        } else {
          if (width === height) {
            this.ratio = 1
          } else if (width < height) {
            this.ratio = 2
          } else {
            this.ratio = 0.5
          }
        }
      },
      setVideoDimension (obj) {
        this.fileInfo.dimension.x = obj.width
        this.fileInfo.dimension.y = obj.height
        this.setRatio(obj.width, obj.height)
      },
      setImageDimensions () {
        if (this.fileInfo.key === '' && this.fileInfo.model === null) {
          this.fileInfo.dimension.x = 0
          this.fileInfo.dimension.y = 0
          this.setRatio()
        }
        // if image
        if (!this.fileInfo.isVideo) {
          const target = new Image()
          target.src = this.fileInfo.url
          target.onload = () => {
            this.fileInfo.dimension.x = target.width
            this.fileInfo.dimension.y = target.height
            this.setRatio(this.fileInfo.dimension.x, this.fileInfo.dimension.y)
          }
        } else {
          // if video
          if (this.fileInfo.dimension === undefined) {
            this.setRatio()
          } else {
            this.setRatio(this.fileInfo.dimension.x, this.fileInfo.dimension.y)
          }
        }
      },
      setError (bool, msg) {
        this.error = bool
        this.errorMsg = msg
      },
      async getUrl (key, isWorkingAlbum) {
        if (this.fileInfo.state !== undefined && this.fileInfo.state === this.STATE_LOCAL) {
          return
        }
        this.fileInfo.url = this.getSignedUrl(key, isWorkingAlbum)
        this.fileInfo.state = this.STATE_SERVER
      },
    },
  }
</script>
<style lang="scss" scoped>
.sheet {
  cursor: pointer;
  border: dashed #999999 !important;
  color: #999999 !important;
  z-index: 1;
  &-error {
    border: dashed #ff5252 !important;
    color: #ff5252 !important;
  }
  &-read-only {
    border: #999999 solid 1px !important;
  }
}
</style>
<style>
.overlap {
  position: relative;
  z-index: 2;
  place-content: flex-end;
}
.display-grid {
  display: grid;
}
.list-image-row {
  border: #999999 solid 1px !important;
  width: 130px;
  height: 130px;
}
.absolute-row {
  position: absolute;
}
</style>
