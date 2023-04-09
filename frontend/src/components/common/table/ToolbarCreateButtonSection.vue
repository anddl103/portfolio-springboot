<template>
  <v-row
    class="ma-auto"
  >
    <v-divider
      class="mx-4"
      inset
      vertical
    />
    <v-btn
      color="primary"
      dark
      @click="create"
    >
      {{ buttonLabel }}
    </v-btn>
  </v-row>
</template>

<script>
  export default {
    name: 'CreateButtonSection',
    components: {},
    props: {
      label: String,
    },
    computed: {
      buttonLabel () {
        return this.label.concat(' ', '추가')
      },
    },
    methods: {
      async create () {
        this.$emit('create')
      },
    },
  }
</script>
