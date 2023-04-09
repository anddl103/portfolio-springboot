<template>
  <v-dialog
    v-model="open"
    max-width="500px"
    @click:outside="dialogClose"
    @keydown.esc="dialogClose"
    @keydown.enter="dialogSubmit"
  >
    <v-card>
      <common-error-alert
        :error="error"
        :error-msg="errorMsg"
        @close="resetError"
      />

      <v-card-title
        v-if="!error"
        class="text-h5"
        v-html="defaultDialogText"
      />
      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          text
          @click="dialogClose"
        >
          Cancel
        </v-btn>
        <v-btn
          v-if="error === false"
          color="blue darken-1"
          text
          @click="dialogSubmit"
        >
          OK
        </v-btn>
        <v-spacer />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script>

  import axios from '@/axios'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import store from '@/store'

  export default {
    name: 'CommonDelDialog',
    components: { CommonErrorAlert },
    mixins: [
      commonErrorMixin,
    ],
    props: {
      open: Boolean,
      target: String,
      url: String,
    },
    data: () => ({
      errorDialogText: '',
    }),
    computed: {
      defaultDialogText () {
        return '삭제 대상 : ' + this.target + ' <br> 정말 이 항목을 삭제하시겠습니까?'
      },
    },
    methods: {
      async dialogSubmit () {
        await this.deleteItemConfirm()
      },
      dialogClose () {
        this.resetError()
        this.$emit('update:open', false)
      },
      async deleteItemConfirm () {
        await store.dispatch('setLoadingTrue')
        await axios.delete(this.url).then(async res => {
          if (!await this.validateResponse(res)) {
            return
          }
          await store.dispatch('setLoadingFalse')
          this.$emit('update:open', false)
          this.$emit('confirm')
        }).catch(err => this.setError(err))
      },
    },
  }
</script>
