<template>
  <v-container
    class="pa-4"
    fluid
  >
    <common-error-alert
      :error="error"
      :error-msg="errorMsg"
      @close="resetError"
    />

    <v-row class="ma-auto">
      <v-col
        cols="1"
      >
        정책
      </v-col>
      <v-col cols="10">
        <v-form
          ref="form"
          v-model="formValid"
          lazy-validation
        >
          <v-row>
            <v-autocomplete
              v-model="targetItem.usage"
              label="사용처"
              item-text="description"
              item-value="code"
              :items="usageCodes"
              :rules="[required]"
              :readonly="!editMode"
              :disabled="editMode && defaultItem.id !== undefined"
              :append-icon="editMode ? undefined : ''"
              :class="editMode ? '' : 'disable-events'"
            />
          </v-row>
          <subject-contents-component
            v-for="lc in localeCodes"
            :key="lc.code"
            :locale-code="lc"
            :default-item.sync="targetItem.localeCodeContents[lc.code]"
            :is-new="defaultItem.id === undefined"
            :read-only="!editMode"
            :reset="resetEditComponent"
            @resetCallback="syncResetState"
          />
        </v-form>
      </v-col>
    </v-row>
    <v-divider
      class="mt-3 mb-10"
    />
    <button-handler
      :cancel-destination="cancelDestination"
      :edit-mode="editMode"
      :target-index="targetIndex"
      @save="save"
      @update="update"
      @modify="toEditMode"
      @cancelModify="cancelModify"
    />
  </v-container>
</template>
<script >
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import router, { routeName } from '@/router'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import SubjectContentsComponent from '@/components/common/editor/SubjectContentsComponent'
  import ButtonHandler from '@/components/common/ButtonHandler'
  import store from '@/store'
  import 'vue-datetime/dist/vue-datetime.css'
  import editorMixin from '@/mixins/editorMixin'
  import rulesMixin from '@/mixins/rulesMixin'
  import commonFormMixin from '@/mixins/commonFormMixin'

  export default {
    name: 'PolicyForm',
    components: {
      ButtonHandler,
      CommonErrorAlert,
      SubjectContentsComponent,
    },
    mixins: [
      commonErrorMixin,
      rulesMixin,
      editorMixin,
      commonFormMixin,
    ],
    data: () => ({
      readOnly: true,
      editMode: false,
      cancelDestination: routeName.POLICY,
      usageCodes: [],
      formValid: true,
      targetItem: JSON.parse(JSON.stringify(store.state.common.targetItem)),
    }),
    computed: {
      handleSave () {
        return Object.entries(this.defaultItem).length === 0
      },
      minDate () {
        return this.$moment().format()
      },
    },
    created () {
      this.initialize()
    },
    methods: {
      initialize () {
        this.getPolicyUsages()
        this.setInitItems()
      },
      async setInitItems () {
        if (this.targetIndex !== -1) {
          this.targetItem = JSON.parse(JSON.stringify(this.defaultItem))
          for (const lc of this.localeCodes) {
            this.targetItem.localeCodeContents[lc.code].isValid = true
          }
        } else {
          this.createEmptyTargetItem()
          this.toEditMode()
        }
      },
      createEmptyTargetItem () {
        const obj = {}
        for (const lc of this.localeCodes) {
          obj[lc.code] = {
            isValid: true,
          }
        }
        this.targetItem = {
          id: '',
          localeCodeContents: obj,
        }
      },
      getPolicyUsages () {
        axios.get(ApiList.usages).then(res => {
          if (!this.validateResponse(res)) return
          this.usageCodes = res.data.data
        }).catch(err => {
          this.setError(err)
        })
      },
      cancelModify () {
        this.handleEditorError(false)
        this.resetTarget()
        this.toReadOnly()
      },
      toEditMode () {
        this.editMode = true
      },
      toReadOnly () {
        this.editMode = false
      },
      async toList () {
        await router.push({ name: routeName.POLICY })
      },
      validation () {
        // 사용처 체크
        const con1 = this.checkForm(this.$refs.form)
        // editors 체크
        const conArr = []
        for (const lc of this.localeCodes) {
          conArr.push(this.targetItem.localeCodeContents[lc.code].isValid)
        }
        return con1 === true && conArr.findIndex(c => c === false) === -1
      },
      update () {
        if (!this.validation()) return
        store.dispatch('setLoadingTrue')
        const req = JSON.parse(JSON.stringify(this.targetItem))
        req.usage = this.targetItem.usage.code
        axios.post(ApiList.policies, req).then(res => {
          if (!this.validateResponse(res)) return
          this.defaultItem = res.data.data
          this.toReadOnly()
        }).catch(err => {
          this.setError(err)
        })
      },
      save () {
        if (!this.validation()) return
        store.dispatch('setLoadingTrue')
        axios.post(ApiList.policies, this.targetItem).then(res => {
          if (!this.validateResponse(res)) return
          this.toList()
        }).catch(err => {
          this.setError(err)
        })
      },
    },
  }
</script>
<style>
.datetimeInput {
  width: inherit;
  background-color: #999999;
}
</style>
