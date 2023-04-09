<template>
  <v-main>
    <v-container fluid>
      <router-view :key="$route.path" />
      <v-overlay
        :value="loading"
        z-index="999"
      >
        <v-progress-circular
          v-if="loading"
          size="100"
          :value="loadingValue"
          color="amber"
        >
          <label class="v-label">{{ loadingValue }}%</label>
        </v-progress-circular>
      </v-overlay>
    </v-container>
  </v-main>
</template>

<script>
  import store from '@/store'

  export default {
    name: 'DefaultView',
    computed: {
      loading () {
        return store.state.common.loading
      },
      loadingValue () {
        return store.state.common.loadingValue
      },
    },
  }
</script>
