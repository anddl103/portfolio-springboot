<template>
  <v-app v-if="this.$route.meta.requiresAuth">
    <default-bar />

    <default-drawer />

    <default-view />

    <!--    <default-footer />-->

    <!-- <default-settings /> -->
  </v-app>
  <v-app v-else>
    <default-view />
  </v-app>
</template>

<script>
  export default {
    name: 'DefaultLayout',
    components: {
      DefaultBar: () => import(
        /* webpackChunkName: "default-app-bar" */
        './AppBar'
      ),
      DefaultDrawer: () => import(
        /* webpackChunkName: "default-drawer" */
        './Drawer'
      ),
      // DefaultFooter: () => import(
      //   /* webpackChunkName: "default-footer" */
      //   './Footer'
      // ),
      // DefaultSettings: () => import(
      //   /* webpackChunkName: "default-settings" */
      //   './Settings'
      // ),
      DefaultView: () => import(
        /* webpackChunkName: "default-view" */
        './View'
      ),
    },
  }
</script>
