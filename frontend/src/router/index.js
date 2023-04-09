// Imports
import Vue from 'vue'
import Router from 'vue-router'
import {
  layout,
  route,
} from '@/util/routes'
import store from '@/store'

Vue.use(Router)

export const routeName = Object.freeze({
  LOGIN: 'Login',
  DASHBOARD: 'Dashboard',
  ADMIN: 'Admin',
  ALBUM: 'Album',
  ALBUM_DETAIL: 'AlbumDetail',
  ALBUM_DEPLOYED: 'AlbumDeployed',
  ALBUM_DEPLOYED_DETAIL: 'AlbumDeployedDetail',
  ORDER: 'Order',
  ORDER_FORM: 'OrderForm',
  BANNER: 'Banner',
  BANNER_FORM: 'BannerForm',
  MY_INFO: 'MyInfo',
  LANGUAGE: 'Language',
  COUNTRY_CODE: 'CountryCode',
  ARTIST: 'Artist',
  ARTIST_FORM: 'ArtistForm',
  NFC: 'Nfc',
  NFC_FORM: 'NfcForm',
  QUESTION: 'Question',
  QUESTION_FORM: 'QuestionForm',
  NOTICE: 'Notice',
  NOTICE_FORM: 'NoticeForm',
  FAQ: 'Faq',
  FAQ_FORM: 'FaqForm',
  FAQ_CATEGORY: 'FaqCategory',
  POLICY: 'Policy',
  POLICY_FORM: 'PolicyForm',
  USER: 'User',
  USER_FORM: 'UserForm',
  DEMO_TOOL: 'DemoTool',
  BATCH_STATE: 'BatchState',
})

const router = new Router({
  mode: 'history',
  // base: process.env.BASE_URL,
  scrollBehavior: (to, from, savedPosition) => {
    if (to.hash) return { selector: to.hash }
    if (savedPosition) return savedPosition

    return { x: 0, y: 0 }
  },
  routes: [
    layout('Default', [
      // 시작
      route(routeName.LOGIN, null, '/login', false),
      route(routeName.DASHBOARD),
      route(routeName.ADMIN, { default: 'admin/Admin' }, '/admin'),
      route(routeName.ALBUM, { default: 'album/Album' }, '/album'),
      route(routeName.ALBUM_DETAIL, { default: 'album/AlbumDetail' }, '/album-detail'),
      route(routeName.ALBUM_DEPLOYED, { default: 'album-deployed/AlbumDeployed' }, '/album-deployed'),
      route(routeName.ALBUM_DEPLOYED_DETAIL, { default: 'album-deployed/AlbumDeployedDetail' }, '/album-deployed-detail'),
      route(routeName.ORDER, { default: 'order/Order' }, '/order'),
      route(routeName.ORDER_FORM, { default: 'order/OrderForm' }, '/order-form'),
      route(routeName.BANNER, { default: 'banner/Banner' }, '/banner'),
      route(routeName.BANNER_FORM, { default: 'banner/BannerForm' }, '/banner-form'),
      route(routeName.MY_INFO, { default: 'my-info/MyInfo' }, '/my-info'),
      route(routeName.LANGUAGE, { default: 'language/Language' }, '/language'),
      route(routeName.COUNTRY_CODE, { default: 'country-code/CountryCode' }, '/country-code'),
      route(routeName.ARTIST, { default: 'artist/Artist' }, '/artist'),
      route(routeName.ARTIST_FORM, { default: 'artist/ArtistForm' }, '/artist-form'),
      route(routeName.NFC, { default: 'nfc/Nfc' }, '/nfc'),
      route(routeName.NFC_FORM, { default: 'nfc/NfcForm' }, '/nfc-form'),
      route(routeName.QUESTION, { default: 'question/Question' }, '/question'),
      route(routeName.QUESTION_FORM, { default: 'question/QuestionForm' }, '/question-form'),
      route(routeName.NOTICE, { default: 'notice/Notice' }, '/notice'),
      route(routeName.NOTICE_FORM, { default: 'notice/NoticeForm' }, '/notice-form'),
      route(routeName.FAQ, { default: 'faq/Faq' }, '/faq'),
      route(routeName.FAQ_FORM, { default: 'faq/FaqForm' }, '/faq-form'),
      route(routeName.FAQ_CATEGORY, { default: 'faq-category/FaqCategory' }, '/faq-category'),
      route(routeName.POLICY, { default: 'policy/Policy' }, '/policy'),
      route(routeName.POLICY_FORM, { default: 'policy/PolicyForm' }, '/policy-form'),
      route(routeName.USER, { default: 'user/User' }, '/user'),
      route(routeName.USER_FORM, { default: 'user/UserForm' }, '/user-form'),
      route(routeName.DEMO_TOOL, { default: 'demo/Tool' }, '/tool'),
      route(routeName.BATCH_STATE, { default: 'batch/Batch' }, '/batch-state'),
    ]),
  ],
})

router.beforeEach((to, from, next) => {
  store.commit('setSelectedMenu', to.path)
  const authRequired = to.matched.some(record => record.meta.requiresAuth)
  if (!authRequired) {
    return next()
  }

  // TODO: verify token ?
  // const currentUser = firebaseAuth.currentUser
  // if (currentUser) {
  // }

  const uid = store.state.auth.uid
  if (uid === null || uid === undefined || uid === '') {
    next({ name: 'Login' })
  }
  next()
})

export default router
