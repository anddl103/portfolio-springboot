<template>
  <v-container
    class="pa-4"
    fluid
  >
    <v-dialog
      v-model="open"
      max-width="500px"
      @click:outside="close"
      @keydown.esc="close"
    >
      <template v-slot:activator="{ }" />
      <v-card>
        <common-error-alert
          :error="error"
          :error-msg="errorMsg"
          @close="resetError"
        />

        <v-card-title>
          <span class="text-h5">1:1 문의 생성</span>
        </v-card-title>
        <v-card-text class="mt-8">
          <v-row>
            <v-text-field
              v-model="title"
              label="제목"
            />
          </v-row>
          <v-row>
            <editor
              v-model="content"
              :show-header="false"
            />
          </v-row>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn
            color="blue darken-1"
            text
            @click="close"
          >
            취소
          </v-btn>
          <v-btn
            color="blue darken-1"
            text
            @click="save"
          >
            저장
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
<script >
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import Editor from '@/components/common/editor/TipTapEditor'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import store from '@/store'
  const constant = Object.freeze({
    eventName: {
      SAVE: 'save',
      CLOSE: 'close',
    },
  })
  export default {
    name: 'QuestionAddForm',
    components: {
      Editor,
      CommonErrorAlert,
    },
    mixins: [
      commonErrorMixin,
    ],
    props: {
      open: Boolean,
    },
    data: () => ({
      readOnly: true,
      title: '',
      content: '',
    }),
    methods: {
      async save () {
        await store.dispatch('setLoadingTrue')
        const req = {
          title: this.title,
          content: this.content,
        }
        axios.post(ApiList.addQuestions, req).then(res => {
          if (!this.validateResponse(res)) return
          this.$emit(constant.eventName.SAVE)
          this.close()
        }).catch(err => this.setError(err))
      },
      close () {
        this.title = ''
        this.content = ''
        this.$emit(constant.eventName.CLOSE)
      },
    },
  }
</script>
