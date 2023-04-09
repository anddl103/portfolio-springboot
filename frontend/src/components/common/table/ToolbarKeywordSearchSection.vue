<template>
  <v-row>
    <v-divider
      class="mx-4"
      inset
      vertical
    />
    <v-text-field
      append-icon="mdi-magnify"
      :value="model"
      :label="label + ' 검색'"
      hide-details
      @input="update"
    />
  </v-row>
</template>

<script>
  export default {
    name: 'KeywordSearchSection',
    components: {},
    props: {
      model: String,
      label: String,
    },
    methods: {
      async update (val) {
        this.$emit('update:model', val)
      },
    },
  }
</script>
