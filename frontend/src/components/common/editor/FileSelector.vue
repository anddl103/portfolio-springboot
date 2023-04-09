<template>
  <v-dialog
    v-model="open"
    width="fit-content"
    @click:outside="close"
  >
    <v-card>
      <v-card-title class="justify-center">
        파일 선택
      </v-card-title>
      <v-card-text>
        <v-input
          :messages="showFileInfo"
          :error="error"
          :error-messages="errorMsg"
        >
          <v-sheet
            class="pa-2"
            :class="handleSheetStyle"
            :width="width"
            :height="height"
            @dragover.prevent
            @dragenter.prevent
            @drop.prevent="onDrop"
            @click="onClickFile"
          >
            <v-row
              v-if="!disabled && imageSrc !== '' && imageSrc !== undefined"
              class="overlap"
            >
              <v-icon
                @click.stop="deleteFile"
              >
                mdi-close
              </v-icon>
            </v-row>
            <input
              ref="fileInput"
              type="file"
              hidden
              accept="image/*"
              @change="onChange"
            >
            <v-responsive
              :aspect-ratio="1"
              class="align-center text-align-center"
            >
              <v-sheet
                v-if="imageSrc"
              >
                <v-img
                  v-model="file"
                  class="preview"
                  contain
                  :aspect-ratio="1"
                  :src="imageSrc"
                />
              </v-sheet>
              <div v-if="disabled && !imageSrc">
                없음
              </div>
              <div
                v-if="!disabled && !imageSrc"
                class="text-align-center"
              >
                Click to choose file <br>
                OR <br>
                Drag & drop here
              </div>
            </v-responsive>
          </v-sheet>
        </v-input>
      </v-card-text>
      <v-card-actions class="justify-center">
        <v-btn @click="attach">
          첨부
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
  import 'vue-md-player/dist/vue-md-player.css'
  import fileUploadMixin from '@/mixins/fileUploadMixin'

  export default {
    name: 'FileUpload',
    mixins: [
      fileUploadMixin,
    ],
    props: {
      open: Boolean,
      width: Number,
      height: Number,
      disabled: Boolean,
    },
    data: () => ({
      imageSrc: '',
      file: null,
      error: false,
      errorMsg: '',
      fileWidth: 0,
      fileHeight: 0,
    }),
    computed: {
      showFileInfo () {
        if (this.fileWidth + this.fileHeight === 0) return ''
        return this.fileWidth + 'x' + this.fileHeight
      },
      handleSheetStyle () {
        if (this.error) {
          return 'sheet-error'
        } else {
          if (this.disabled) {
            return ''
          } else {
            return 'sheet'
          }
        }
      },
    },
    methods: {
      close () {
        this.deleteFile()
        this.$emit('close')
      },
      deleteFile (e) {
        this.$refs.fileInput.value = null
        this.imageSrc = ''
        this.file = null
        this.setImageDimensions(null)
      },
      onDrop (event) {
        const file = event.dataTransfer.files[0]
        this.file = file
        this.preview(file)
      },
      onChange (event) {
        const file = event.target.files[0]
        this.file = file
        this.preview(file)
      },
      onClickFile (e) {
        if (this.disabled) return
        this.$refs.fileInput.click()
      },
      preview (file) {
        if (typeof file === 'string') {
          this.imageSrc = file
        } else {
          if (!this.validateFileFormat(file, 'IMAGE')) {
            this.error = true
            this.imageSrc = ''
            this.errorMsg = '사용 가능한 확장자가 아닙니다.'
            this.setImageDimensions(null)
            this.setImageAsBase64(null)
          } else {
            this.error = false
            this.errorMsg = ''
            this.setImageDimensions(file)
            this.setImageAsBase64(file)
          }
        }
      },
      attach () {
        if (this.error) return
        const fileInfo = {
          file: this.file,
          url: this.imageSrc,
        }
        this.$emit('attach', fileInfo)
        this.close()
      },
      setImageAsBase64 (file) {
        if (file === null) {
          this.imageSrc = ''
          return
        }
        const reader = new FileReader()
        // convert the file to base64 text
        reader.readAsDataURL(file)
        reader.onload = () => {
          this.imageSrc = reader.result
        }
      },
      setImageDimensions (file) {
        if (file === null) {
          this.fileWidth = 0
          this.fileHeight = 0
          return
        }
        const target = new Image()
        target.src = URL.createObjectURL(file)
        target.onload = () => {
          this.fileWidth = target.width
          this.fileHeight = target.height
        }
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
}

</style>
<style>
.fill-width {
  width: 100%
}
.vplayer {
  pointer-events: none;
}
.overlap {
  position: relative;
  z-index: 2;
  place-content: flex-end;
}
</style>
