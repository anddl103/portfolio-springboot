<template>
  <v-btn
    v-model="show"
    class="ml-3 mr-4"
    elevation="1"
    fab
    small
    @click="show = !show"
  >
    <v-icon>
      mdi-format-list-bulleted
    </v-icon>
  </v-btn>
</template>

<script>

  import store from '@/store'

  export default {
    name: 'DefaultDrawerToggle',

    computed: {
      show: {
        get () {
          return store.state.common.sideNavi
        },
        set (val) {
          store.commit('setSideNavi', val)
        },
      },
    },
  }
</script>
