<template>
  <v-menu
    bottom
    min-width="200px"
    rounded
    offset-y
  >
    <template v-slot:activator="{ on }">
      <v-btn
        class="ma-3"
        icon
        v-on="on"
      >
        <v-avatar size="40">
          <img
            :src="profileImage"
            :alt="nickName"
          >
        </v-avatar>
      </v-btn>
    </template>
    <v-card class="d-flex flex-column align-center">
      <v-col>
        <span class="ma-2">
          <span>사용자 정보</span>
        </span>
        <admin-info-change />
      </v-col>
      <v-col>
        <span class="ma-2">
          <span>비밀번호</span>
          <admin-pw-change />
        </span>
      </v-col>
      <v-col>
        <span class="ma-3">
          <v-btn
            class="mx-3"
            depressed
            outlined
            @click="logout()"
          >
            Logout
          </v-btn>
        </span>
      </v-col>
    </v-card>
  </v-menu>
</template>
<script>
  import AdminInfoChange from './AdminInfoChange.vue'
  import AdminPwChange from './AdminPwChange.vue'
  import { firebaseAuth } from '@/firebase'
  import router from '@/router'
  import store from '@/store'

  export default {
    name: 'AdminAvata',
    components: { AdminPwChange, AdminInfoChange },
    computed: {
      profileImage () {
        return this.$store.getters.getUserInfo.profileImage
      },
      nickName () {
        return this.$store.getters.getUserInfo.nickName
      },
    },
    methods: {
      async logout () {
        console.log('------------- logout method -------------')
        firebaseAuth.signOut().then(async () => {
          await store.commit('setLoginFlag', false)
          await router.push({ name: 'Login' })
        }).catch(() => {
          console.log('logout fail')
        })
      },
    },
  }
</script>
