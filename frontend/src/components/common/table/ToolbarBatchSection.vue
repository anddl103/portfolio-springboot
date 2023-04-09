<template>
  <v-row
    class="ma-auto"
  >
    <v-divider
      class="mx-4"
      inset
      vertical
    />
    <v-row
      class="ma-auto max-width-fit-contents"
    >
      {{ batchStateText }}
    </v-row>
    <v-divider
      class="mx-4"
      inset
      vertical
    />
    <v-btn
      v-if="executeHandler"
      color="primary"
      dark
      @click="beforeExecute"
    >
      {{ '강제실행' }}
    </v-btn>
    <v-divider
      v-if="executeHandler"
      class="mx-4"
      inset
      vertical
    />
    <v-spacer />
    <v-divider
      v-if="executeHandler"
      class="mx-4"
      inset
      vertical
    />
    <v-btn
      v-if="executeHandler"
      color="primary"
      dark
      @click="toggle"
    >
      {{ toolbarButtonText }}
    </v-btn>
    <common-error-alert
      :error="error"
      :error-msg="errorMsg"
      @close="resetError"
    />
    <common-confirm-dialog
      :dialog-handler.sync="confirmDialog"
      :dialog-content="confirmDialogContent"
      @confirm="execute"
    />
    <v-dialog
      v-model="feedbackDialog"
      max-width="500px"
      @click:outside="closeFeedbackDialog"
      @keydown.esc="closeFeedbackDialog"
      @keydown.enter="closeFeedbackDialog"
    >
      <v-card>
        <v-card-title class="text-h5">
          {{ feedbackDialogText }}
        </v-card-title>
        <v-card-actions>
          <v-btn
            class="ma-auto"
            color="blue darken-1"
            text
            @click="closeFeedbackDialog"
          >
            확인
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
  import store from '@/store'
  import { batchState, role } from '@/assets/enums'
  import ApiList from '@/axios/api-list'
  import axios from '@/axios/index'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import CommonConfirmDialog from '@/components/common/CommonConfirmDialog'
  import commonConfirmMixin from '@/mixins/commonConfirmMixin'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  const title = '배치'

  export default {
    name: 'BatchSection',
    components: {
      CommonErrorAlert,
      CommonConfirmDialog,
    },
    mixins: [
      commonConfirmMixin,
      commonErrorMixin,
    ],
    props: {
      batchDone: { type: Boolean, default: false },
    },
    data: () => ({
      feedbackDialog: false,
      feedbackDialogText: '',
      executeHandler: false,
      toolbarButtonText: '',
      newState: null,
    }),
    computed: {
      batchStateText () {
        return title.concat(' ', this.toolbarButtonState.description)
      },
      toolbarButtonState () {
        return store.state.common.batchState
      },
    },
    mounted () {
      if (this.batchDone) {
        this.$emit('update:batchDone', false)
      }
      this.decider()
    },
    methods: {
      decider () {
        if (store.state.auth.currentRole === role.CONTENTS_MANAGER) {
          switch (this.toolbarButtonState.code) {
            case batchState.OFF.code:
              this.toolbarButtonText = title + ' 가동'
              this.executeHandler = false
              this.newState = true
              this.feedbackDialogText = '배치를 정지했습니다.'
              break
            case batchState.WORKING.code:
            case batchState.ON.code:
              this.toolbarButtonText = title + ' 정지'
              this.executeHandler = true
              this.newState = false
              this.feedbackDialogText = '배치를 가동하여 대기상태에 있습니다.'
              break
            default:
              this.toolbarButtonText = 'ERROR'
              this.executeHandler = false
              this.newState = false
              this.feedbackDialogText = ''
          }
        } else {
          this.executeHandler = false
        }
      },
      async handleBatchButton () {
        const url = ApiList.batchState
        const req = {
          jobEnabled: this.newState,
        }
        await axios.patch(url, req).then(async res => {
          await store.commit('setBatchState', res.data.data.batchState)
          await this.decider()
          this.openFeedbackDialog()
        }).catch(err => {
          this.setError(err)
        })
      },
      toggle () {
        this.handleBatchButton()
      },
      beforeExecute () {
        const dialogText = '배치를 즉시 실행하시겠습니까?'
        this.openConfirmDialog(dialogText)
      },
      execute () {
        store.dispatch('setLoadingTrue')
        const url = ApiList.batchExecute
        axios.post(url).then(async () => {
          await store.dispatch('setLoadingFalse')
          this.feedbackDialogText = '배치를 실행했습니다.'
          await this.openFeedbackDialog()
          this.$emit('update:batchDone', true)
        }).catch(err => {
          store.dispatch('setLoadingFalse')
          this.$emit('error', err)
        })
      },
      openFeedbackDialog () {
        this.feedbackDialog = true
      },
      closeFeedbackDialog () {
        this.feedbackDialogText = ''
        this.feedbackDialog = false
      },
    },
  }
</script>
