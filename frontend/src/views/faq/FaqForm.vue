<template>
  <v-container
    v-if="targetItemPrepared"
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
        FAQ
      </v-col>
      <v-col cols="10">
        <v-form
          ref="form"
          v-model="formValid"
          lazy-validation
        >
          <v-autocomplete
            v-model="targetItem.category"
            label="카테고리"
            item-text="comment"
            item-value="id"
            :items="categories"
            :rules="[required]"
            :readonly="readOnly"
            :append-icon="readOnly ? '' : undefined"
          />
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
      @modify="toModify"
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
  import rulesMixin from '@/mixins/rulesMixin'
  import editorMixin from '@/mixins/editorMixin'
  import commonFormMixin from '@/mixins/commonFormMixin'

  export default {
    name: 'FaqForm',
    components: {
      CommonErrorAlert,
      SubjectContentsComponent,
      ButtonHandler,
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
      categories: store.state.common.faqCategories,
      selectedCategory: '',
      selectedLocaleCode: '',
      formValid: true,
      targetItem: {},
      cancelDestination: routeName.FAQ,
    }),
    computed: {
      isCreate () {
        return Object.entries(this.defaultItem).length === 0
      },
      targetItemPrepared: {
        get () {
          return this.targetItem.localeCodeContents !== undefined
        },
      },
    },
    created () {
      this.initialize()
    },
    methods: {
      async initialize () {
        await this.setInitItems()
      },
      async setInitItems () {
        if (this.targetIndex !== -1) {
          this.targetItem = JSON.parse(JSON.stringify(this.defaultItem))
          this.selectedCategory = this.targetItem.category.code
          this.selectedLocaleCode = this.targetItem.localeCode
        } else {
          await this.createEmptyTargetItem()
          this.toModify()
        }
      },
      createEmptyTargetItem () {
        const obj = {}
        for (const lc of this.localeCodes) {
          obj[lc.code] = {}
        }
        this.targetItem = {
          id: '',
          localeCodeContents: obj,
        }
      },
      async cancelModify () {
        this.handleEditorError(false)
        this.resetTarget()
        await this.toReadOnly()
      },
      toModify () {
        this.readOnly = false
        this.editMode = true
      },
      toReadOnly () {
        this.readOnly = true
        this.editMode = false
      },
      async toList () {
        await router.push({ name: routeName.FAQ })
      },
      validation () {
        const condition = this.checkForm(this.$refs.form)
        const conArr = []
        for (const lc of this.localeCodes) {
          if (this.targetItem.localeCodeContents) {
            const obj = this.targetItem.localeCodeContents[lc.code]
            const tempCon1 = this.checkEditForm(obj.subject)
            const tempCon2 = this.checkEditForm(obj.contents)
            conArr.push(tempCon1, tempCon2)
          } else {
            conArr.push(false)
          }
        }
        return condition && !conArr.includes(a => a === false)
      },
      update () {
        if (!this.validation()) return
        store.dispatch('setLoadingTrue')
        this.targetItem.category = this.selectedCategory
        this.targetItem.localeCode = this.selectedLocaleCode
        axios.patch(ApiList.faqWithId(this.targetItem.id), this.targetItem).then(res => {
          if (!this.validateResponse(res)) return
          this.defaultItem = JSON.parse(JSON.stringify(res.data.data))
          this.toReadOnly()
        }).catch(err => {
          this.setError(err)
        })
      },
      save () {
        if (!this.validation()) return
        store.dispatch('setLoadingTrue')
        axios.post(ApiList.faqs, this.targetItem).then(res => {
          if (!this.validateResponse(res)) return
          this.toList()
        }).catch(err => {
          this.setError(err)
        })
      },
    },
  }
</script>
