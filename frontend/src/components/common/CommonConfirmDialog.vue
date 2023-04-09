<template>
  <v-dialog
    v-model="dialogHandler"
    max-width="500px"
    @click:outside="close"
    @keydown.esc="close"
    @keydown.enter="submit"
  >
    <v-card>
      <v-card-title
        class="text-h5"
        v-html="dialogContent"
      />
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
          확인
        </v-btn>
        <v-spacer />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script>

  export default {
    name: 'CommonConfirmDialog',
    props: {
      dialogHandler: Boolean,
      dialogContent: String,
    },
    methods: {
      async submit () {
        await this.$emit('confirm')
        this.close()
      },
      close () {
        this.$emit('update:dialogHandler', false)
      },
    },
  }
</script>
