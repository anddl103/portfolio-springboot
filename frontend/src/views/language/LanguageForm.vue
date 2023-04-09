<template>
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
        <span class="text-h5">{{ formTitle }}</span>
      </v-card-title>
      <v-card-text class="mt-8">
        <v-form
          ref="dialogForm"
          v-model="dialogValid"
          lazy-validation
        >
          <v-text-field
            v-for="(val, i) in localeCodes"
            :key="i"
            v-model="item.values[val.code]"
            :label="val.comment"
            outlined
          >
            {{ val }}
          </v-text-field>
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
          @click="targetIndex === -1 ? save() : update()"
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
  const constant = Object.freeze({
    eventName: {
      CLOSE: 'close',
      AFTER_SAVE: 'afterSave',
      AFTER_UPDATE: 'afterUpdate',
    },
  })

  export default {
    name: 'LanguageForm',
    components: {
      CommonErrorAlert,
    },
    mixins: [
      commonErrorMixin,
    ],
    props: {
      open: Boolean,
      targetItem: {
        type: Object,
        default: () => ({
          id: '',
          values: {},
        }),
      },
      targetIndex: Number,
      formTitle: String,
      tableData: Array,
    },
    data: () => ({
      dialogValid: true,
      localeCodes: store.state.common.localeCodes,
      item: {
        id: '',
        values: {},
      },
    }),
    watch: {
      targetItem () {
        this.init()
      },
    },
    mounted () {
      this.init()
    },
    methods: {
      async init () {
        if (Object.keys(this.targetItem).length <= 0) {
          for await (const cc of this.localeCodes) {
            this.item.values[cc.code] = ''
          }
        } else {
          this.item = JSON.parse(JSON.stringify(this.targetItem))
        }
      },
      close () {
        this.$refs.dialogForm.resetValidation()
        this.item = {
          id: '',
          values: {},
        }
        this.$emit(constant.eventName.CLOSE)
      },
      update () {
        if (!this.$refs.dialogForm.validate()) {
          return
        }
        store.dispatch('setLoadingTrue')
        const id = this.targetItem.id
        const data = this.item
        axios.patch(ApiList.languageWithId(id), data).then(res => {
          if (!this.validateResponse(res)) return
          this.$emit(constant.eventName.AFTER_UPDATE, res.data.data)
          this.close()
        }).catch(err => {
          this.setError(err)
        })
      },
      save () {
        store.dispatch('setLoadingTrue')
        const data = {
          values: this.item.values,
        }

        axios.post(ApiList.languagePacks, data).then(res => {
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
