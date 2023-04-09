<template>
  <v-app-bar
    id="default-app-bar"
    app
    absolute
    class="v-bar--underline"
    color="transparent"
    :clipped-left="$vuetify.rtl"
    :clipped-right="!$vuetify.rtl"
    height="70"
    flat
  >
    <!--    <default-drawer-toggle class="hidden-md-and-up" />-->
    <default-drawer-toggle />
  </v-app-bar>
</template>

<script>
  import store from '@/store'

  export default {
    name: 'DefaultBar',
    components: {
      DefaultDrawerToggle: () => import(
        './widgets/DrawerToggle'
        ),
    },
    data: () => ({
      drawer: {
        image: 0,
        gradient: 0,
        mini: store.state.common.sideNavi,
      },
    }),
  }
</script>
