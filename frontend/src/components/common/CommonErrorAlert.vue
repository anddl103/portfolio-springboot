<template>
  <v-alert
    v-if="error"
    v-model="error"
    type="error"
    dismissible
    text
    @toggle="close"
  >
    {{ errorMsg }}

    <template
      v-slot:prepend
    >
      <v-icon
        color="error"
        class="mr-3"
      >
        mdi-alert-octagon
      </v-icon>
    </template>
    <template
      v-slot:close
    >
      <v-btn
        class="ml-4"
        icon
        small
        color="error"
        @click="close"
      >
        <v-icon>
          mdi-close-circle
        </v-icon>
      </v-btn>
    </template>
  </v-alert>
</template>

<script>
  export default {
    name: 'CommonErrorAlert',
    props: {
      error: { type: Boolean, default: false },
      errorMsg: String,
    },
    methods: {
      close () {
        this.$emit('close')
      },
    },
  }
</script>
