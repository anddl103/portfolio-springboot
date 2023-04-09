<template>
  <v-row
    class="ma-auto"
    align-content="center"
    justify="center"
  >
    <v-btn
      class="mb-3"
      :disabled="modifyDisable"
      @click="handleSubmitButton"
    >
      {{ editMode ? '저장' : modifyText }}
    </v-btn>
    <v-btn
      class="mb-3"
      @click="handleCancelButton(cancelDestination)"
    >
      {{ editMode ? '취소' : '뒤로' }}
    </v-btn>
  </v-row>
</template>

<script>
  export default {
    name: 'ButtonHandler',
    components: {},
    props: {
      cancelDestination: {
        type: String,
        default: '',
      },
      editMode: {
        type: Boolean,
        default: false,
      },
      targetIndex: {
        type: Number,
        default: -1,
      },
      modifyText: {
        type: String,
        default: '수정',
      },
      modifyDisable: {
        type: Boolean,
        default: false,
      },
    },
    methods: {
      async handleSubmitButton () {
        if (this.editMode && this.targetIndex === -1) {
          // save
          this.$emit('save')
        }
        if (this.editMode && this.targetIndex > -1) {
          // modify
          this.$emit('update')
        }
        if (!this.editMode) {
          // to modify
          this.$emit('modify')
        }
      },
      async handleCancelButton (name) {
        // 취소
        if (this.editMode) {
          if (this.targetIndex < 0) {
            await this.$router.push({ name: name })
          } else {
            this.$emit('cancelModify')
          }
        // 뒤로
        } else {
          await this.$router.push({ name: name })
        }
      },
    },
  }
</script>
