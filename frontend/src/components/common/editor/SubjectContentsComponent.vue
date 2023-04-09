<template>
  <v-card
    class="pa-0 my-3"
    outlined
  >
    <v-row class="ma-4">
      <v-col cols="1">
        {{ localeCode.comment }}
      </v-col>
      <v-col cols="10">
        <v-form
          ref="form"
          v-model="formValid"
          lazy-validation
        >
          <v-text-field
            v-model="targetItem.subject"
            label="제목"
            :rules="[required]"
            :readonly="readOnly"
            @input="syncItem"
          />
          <editor
            v-model="targetItem.contents"
            :read-only="readOnly"
            :attach-image="true"
            :has-error="editorHasError"
            @setHasError="handleEditorError"
            @input="syncItem"
          />
        </v-form>
      </v-col>
    </v-row>
  </v-card>
</template>
<script >
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import Editor from '@/components/common/editor/TipTapEditor'
  import rulesMixin from '@/mixins/rulesMixin'
  import editorMixin from '@/mixins/editorMixin'

  export default {
    name: 'SubjectContentsComponent',
    components: {
      Editor,
    },
    mixins: [
      commonErrorMixin,
      rulesMixin,
      editorMixin,
    ],
    props: {
      localeCode: Object,
      defaultItem: Object,
      readOnly: Boolean,
      isNew: Boolean,
      reset: Boolean,
    },
    data: () => ({
      formValid: true,
      targetItem: {},
    }),
    computed: {
      showButton () {
        return this.targetItem.subtitle !== '' || this.targetItem.contents !== ''
      },
    },
    watch: {
      formValid () {
        this.targetItem.isValid = this.formValid
      },
      reset () {
        if (this.reset) {
          this.targetItem = JSON.parse(JSON.stringify(this.defaultItem))
          this.$emit('resetCallback', true)
        }
      },
    },
    created () {
      this.initialize()
    },
    methods: {
      async initialize () {
        await this.prepareTargetItem()
      },
      async prepareTargetItem () {
        // new
        if (this.isNew) {
          this.targetItem = {
            subject: '',
            contents: '',
            isValid: true,
          }
        } else {
          this.targetItem = JSON.parse(JSON.stringify(this.defaultItem))
        }
      },
      syncItem () {
        this.$emit('update:defaultItem', this.targetItem)
      },
    },
  }
</script>
