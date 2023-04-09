<template>
  <v-navigation-drawer
    id="default-drawer"
    v-model="drawer"
    :dark="dark"
    app
    width="260"
  >
    <v-row class="ma-2">
      <v-col
        cols="12"
        class="text-h3 ma-2"
      >
        <strong class="mr-1 font-weight-black">Larva</strong>
        <span class="primary--text">Web</span>
      </v-col>
      <v-divider class="my-1 px-12" />
      <default-list
        :items="filteredItems"
      />
    </v-row>
  </v-navigation-drawer>
</template>

<script>
  import router, { routeName } from '@/router'
  import store from '@/store'
  import { role } from '@/assets/enums'

  export default {
    name: 'DefaultDrawer',
    components: {
      DefaultList: () => import(
        /* webpackChunkName: "default-list" */
        './List'
        ),
    },
    data () {
      return {
        dark: false,
        items: [
          {
            title: '대쉬보드',
            icon: 'mdi-view-dashboard',
            to: '/',
          },
          {
            title: '어드민',
            icon: 'mdi-account',
            to: '/admin',
            roles: [role.SUPER_ADMIN, role.VIEWER],
          },
          {
            title: '아티스트',
            icon: 'mdi-account-music',
            to: '/artist',
            roles: [role.CONTENTS_EDITOR, role.CONTENTS_MANAGER, role.PRODUCT_MANAGER, role.VIEWER],
          },
          // {
          //   title: '카드',
          //   icon: 'mdi-sim',
          //   to: '/card',
          // },
          {
            title: '앨범',
            icon: 'mdi-album',
            to: '/album',
            roles: [role.CONTENTS_EDITOR, role.CONTENTS_MANAGER, role.VIEWER],
          },
          {
            title: '배포된 앨범',
            icon: 'mdi-album',
            to: '/album-deployed',
            roles: [role.CONTENTS_EDITOR, role.CONTENTS_MANAGER, role.PRODUCT_MANAGER, role.VIEWER],
          },
          {
            title: '배너',
            icon: 'mdi-open-in-new',
            to: '/banner',
            roles: [role.CONTENTS_EDITOR, role.CONTENTS_MANAGER, role.VIEWER],
          },
          {
            title: 'NFC 카드',
            icon: 'mdi-nfc',
            to: '/nfc',
            roles: [role.PRODUCT_MANAGER, role.VIEWER],
          },
          {
            title: '주문',
            icon: 'mdi-cart',
            to: '/order',
            roles: [role.PRODUCT_MANAGER, role.VIEWER],
          },
          {
            title: 'FAQ',
            icon: 'mdi-help-circle',
            to: '/faq',
            roles: [role.CS_MANAGER, role.VIEWER],
          },
          {
            title: 'FAQ 카테고리',
            icon: 'mdi-help-circle-outline',
            to: '/faq-category',
            roles: [role.CS_MANAGER, role.VIEWER],
          },
          {
            title: '공지',
            icon: 'mdi-bell',
            to: '/notice',
            roles: [role.CS_MANAGER, role.VIEWER],
          },
          {
            title: '1:1 문의',
            icon: 'mdi-frequently-asked-questions',
            to: '/question',
            roles: [role.CS_MANAGER, role.VIEWER],
          },
          {
            title: '정책',
            icon: 'mdi-script-text-outline',
            to: '/policy',
            roles: [role.CS_MANAGER, role.VIEWER],
          },
          {
            title: '다국어',
            icon: 'mdi-translate',
            to: '/language',
            roles: [role.CONTENTS_EDITOR, role.CONTENTS_MANAGER, role.PRODUCT_MANAGER, role.CS_MANAGER, role.VIEWER],
          },
          {
            title: '유저조회',
            icon: 'mdi-account-box-multiple',
            to: '/user',
            roles: [role.CS_MANAGER],
          },
          {
            title: '국가 코드',
            icon: 'mdi-web',
            to: '/country-code',
            roles: [role.CONTENTS_EDITOR, role.CONTENTS_MANAGER, role.PRODUCT_MANAGER, role.VIEWER],
          },
          {
            title: '내 정보 (토큰 확인)',
            icon: 'mdi-shield-account',
            to: '/my-info',
            roles: [role.SUPER_ADMIN, role.CONTENTS_EDITOR, role.CONTENTS_MANAGER, role.PRODUCT_MANAGER, role.CS_MANAGER, role.VIEWER],
          },
          {
            title: '앨범 배치 상태',
            icon: 'mdi-archive-arrow-up',
            to: '/batch-state',
            roles: [role.CONTENTS_EDITOR, role.CONTENTS_MANAGER, role.VIEWER],
          },
          {
            title: 'Demo tool',
            icon: 'mdi-hammer-wrench',
            to: '/tool',
            roles: [role.PRODUCT_MANAGER],
          },
          {
            title: '로그아웃',
            icon: 'mdi-logout',
            to: '/login',
            color: 'secondary',
          },
        ],
      }
    },
    computed: {
      filteredItems () {
        const role = store.state.auth.currentRole
        const filtered = this.items.filter(it => it.roles === undefined || it.roles.findIndex(r => r === role) !== -1)
        return filtered || []
      },
      drawer: {
        get () {
          return store.state.common.sideNavi
        },
        set (val) {
          if (this.drawer !== val) {
            store.commit('setSideNavi', val)
          }
        },
      },
    },
    methods: {
      async logout () {
        console.log('------------- logout method -------------')
        // 로그인 페이지에 들어가기 직전에 라우터가드가 로그아웃 처리를 실행하기 때문에 여기서는 아무 동작을 하지 않는다.
        // await this.$store.dispatch('logout')
        await router.push({ name: routeName.LOGIN })
      },
    },
  }
</script>

<style lang="sass">

#default-drawer
  .v-list-item
    margin-bottom: 8px

  .v-list-item::before,
  .v-list-item::after
    display: none

  .v-list-group__header__prepend-icon,
  .v-list-item__icon
    margin-top: 12px
    margin-bottom: 12px
    margin-left: 4px

  &.v-navigation-drawer--mini-variant
    .v-list-item
      justify-content: flex-start !important
</style>
