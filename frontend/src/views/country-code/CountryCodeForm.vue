<template>
  <v-dialog
    v-model="open"
    max-width="500px"
    @click:outside="close"
    @keydown.esc="close"
    @keydown.enter.prevent="submit"
  >
    <template v-slot:activator="{ }" />
    <v-card>
      <common-error-alert
        :error="error"
        :error-msg="errorMsg"
        @close="resetError"
      />
      <v-card-title>
        <span class="text-h5">{{ targetIndex === -1 ? '생성' : '수정' }}</span>
      </v-card-title>
      <v-card-text class="mt-8">
        <v-form
          ref="dialogForm"
          v-model="dialogValid"
          lazy-validation
        >
          <v-text-field
            v-model="targetItem.code"
            outlined
            label="코드"
            :rules="codeRule"
          />
          <v-text-field
            v-model="targetItem.comment"
            outlined
            label="설명"
            :rules="descriptionRule"
          />
        </v-form>
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
          @click="submit"
        >
          저장
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script >
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import store from '@/store'
  import commonFormMixin from '@/mixins/commonFormMixin'
  const constant = Object.freeze({
    eventName: {
      CLOSE: 'close',
      AFTER_SAVE: 'afterSave',
      AFTER_UPDATE: 'afterUpdate',
    },
  })

  export default {
    name: 'Language',
    components: {
      CommonErrorAlert,
    },
    mixins: [
      commonErrorMixin,
      commonFormMixin,
    ],
    props: {
      open: Boolean,
      tableData: Array,
    },
    data: () => ({
      dialogValid: true,
      codeRule: [
        v => !!v || '필수 항목입니다.',
        v => /^[a-z]{2}$/.test(v) || '코드는 2자리 소문자 영문이어야 합니다.',
      ],
      descriptionRule: [
        v => !!v || '필수 항목입니다.',
      ],
      targetItem: {},
    }),
    computed: {
      defaultItem: {
        get () {
          return store.state.common.targetItem
        },
        set (item) {
          this.$store.commit('setTargetItem', item)
        },
      },
      targetIndex: {
        get () {
          return store.state.common.targetIndex
        },
        set (idx) {
          this.$store.commit('setTargetIndex', idx)
        },
      },
    },
    watch: {
      open () {
        if (this.open) {
          if (this.defaultItem.id !== undefined) {
            this.targetItem = JSON.parse(JSON.stringify(this.defaultItem))
          }
        } else {
          store.commit('resetTargetItem')
          this.targetItem = JSON.parse(JSON.stringify(this.defaultItem))
        }
      },
    },
    methods: {
      submit () {
        this.validate()
        if (this.dialogValid) {
          if (this.targetIndex === -1) {
            this.save()
          } else {
            this.update()
          }
        } else {
          this.close()
        }
      },
      close () {
        this.$refs.dialogForm.resetValidation()
        this.$emit(constant.eventName.CLOSE)
      },
      validate () {
        if (JSON.stringify(this.targetItem) === JSON.stringify(this.defaultItem)) {
          this.dialogValid = false
        }
        this.$refs.dialogForm.validate()
      },
      update () {
        store.dispatch('setLoadingTrue')
        const id = this.targetItem.id
        axios.patch(ApiList.countryWithId(id), this.targetItem).then(res => {
          if (!this.validateResponse(res)) return
          this.$emit(constant.eventName.AFTER_UPDATE, res.data.data)
          this.close()
        }).catch(err => {
          this.setError(err)
        })
      },
      save () {
        store.dispatch('setLoadingTrue')
        axios.post(ApiList.countries, this.targetItem).then(res => {
          if (!this.validateResponse(res)) return
          this.$emit(constant.eventName.AFTER_SAVE, res.data.data)
          this.close()
        }).catch(err => {
          this.setError(err)
        })
      },
    },
  }
</script>
<style>
</style>
