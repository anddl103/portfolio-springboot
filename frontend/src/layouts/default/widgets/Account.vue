<template>
  <v-menu
    bottom
    left
    min-width="200"
    offset-y
    origin="top right"
    transition="scale-transition"
  >
    <template v-slot:activator="{ attrs, on }">
      <v-btn
        class="ml-2"
        min-width="0"
        text
        v-bind="attrs"
        v-on="on"
      >
        <v-icon>mdi-account</v-icon>
      </v-btn>
    </template>

    <v-list
      :tile="false"
      flat
      nav
    >
      <app-bar-item>
        <v-list-item-title
          v-text="profile[0].title"
        />
        <admin-info-change />
      </app-bar-item>

      <app-bar-item>
        <v-list-item-title
          v-text="profile[1].title"
        />
        <admin-pw-change />
      </app-bar-item>
      <v-divider
        class="mb-2 mt-2"
      />
      <app-bar-item>
        <v-list-item-title
          @click="logout"
          v-text="profile[3].title"
        />
      </app-bar-item>

      <!-- <template v-for="(p, i) in profile">
        <v-divider
          v-if="p.divider"
          :key="`divider-${i}`"
          class="mb-2 mt-2"
        />

        <app-bar-item
          v-else
          :key="`item-${i}`"
        >
          <v-list-item-title
            @click="p.action"
            v-text="p.title"
          />
        </app-bar-item>
      </template> -->
    </v-list>
  </v-menu>
</template>

<script>
  import AdminInfoChange from '@/components/auth/AdminInfoChange.vue'
  import AdminPwChange from '@/components/auth/AdminPwChange.vue'
  import router from '@/router'

  export default {
    name: 'DefaultAccount',
    components: { AdminPwChange, AdminInfoChange },
    data: () => ({
      infoDialog: false,
      pwDialog: false,
      profile: [
        { title: '사용자 정보' },
        { title: '비밀번호' },
        { divider: true },
        { title: 'Log out' },
      ],
    }),
    methods: {
      async logout () {
        console.log('------------- logout method -------------')
        try {
          await this.$store.dispatch('logout')
          await router.push({ name: 'Login' })
        } catch (e) {
          console.log('logout fail')
        }
      },
    },
  }
</script>
