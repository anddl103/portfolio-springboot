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
            v-for="(lc, i) in localeCodes"
            :key="i"
            v-model="targetItem.localeCodeSubject[lc.code].subject"
            outlined
            :label="lc.comment"
          />
          <v-text-field
            v-model="targetItem.comment"
            outlined
            label="설명"
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

  export default {
    name: 'FaqCategoryDialog',
    components: {
      CommonErrorAlert,
    },
    mixins: [
      commonErrorMixin,
      commonFormMixin,
    ],
    props: {
      open: Boolean,
    },
    data: () => ({
      dialogValid: true,
      localeCodes: store.state.common.localeCodes,
    }),
    computed: {
      targetItem: {
        get () {
          let obj = {}
          if (this.defaultItem.id !== undefined) {
            obj = JSON.parse(JSON.stringify(this.defaultItem))
          } else {
            const arr = store.state.common.localeCodes
            obj.localeCodeSubject = {}
            obj.comment = ''
            for (const lc of arr) {
              obj.localeCodeSubject[lc.code] = {
                subject: '',
              }
            }
          }
          return obj
        },
        set (val) {
          store.commit('setTargetItem', val)
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
        this.$refs.dialogForm.reset()
        this.$emit('update:open', false)
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
        axios.patch(ApiList.faqCategoryWithId(id), this.targetItem).then(res => {
          if (!this.validateResponse(res)) return
          this.$emit('submit')
          this.close()
        }).catch(err => {
          this.setError(err)
        })
      },
      save () {
        store.dispatch('setLoadingTrue')
        axios.post(ApiList.faqCategories, this.targetItem).then(res => {
          if (!this.validateResponse(res)) return
          this.$emit('submit')
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
