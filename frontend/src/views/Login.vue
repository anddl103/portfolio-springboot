<template>
  <v-app id="inspire">
    <v-main class="login d-flex align-center">
      <login-form />
    </v-main>
  </v-app>
</template>

<script>
  import LoginForm from '@/components/auth/LoginForm.vue'
  import store from '@/store/index'

  export default {
    name: 'Login',
    components: { LoginForm },

    async beforeRouteEnter (to, from, next) {
      console.log('------------- before route login -------------')
      await store.dispatch('logout')
      next()
    },
  }
</script>
