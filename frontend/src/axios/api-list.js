const ApiList = {
  demo: '/demo/ready',
  roles: '/codes/admin/role',
  getUrlByKey: '/storage/preSignedUrl',
  regions: '/codes/region',
  usages: '/codes/usage',
  categories: '/codes/category',
  contentsTypes: '/codes/contents-type',
  albumState: '/codes/album-state',
  languagePacks: '/languagePacks',
  deleteMembers: '/languagePacks/members',
  uploadLanguagePack: '/languagePacks/file/upload',
  downloadLanguagePack: 'http://localhost:8080/api/languagePacks/file/download',
  languageWithId (id) {
    return `${this.languagePacks}/${id}`
  },
  admins: '/admins',
  adminWithId (id) {
    return `${this.admins}/${id}`
  },
  artists: '/artists',
  artistSort: '/artists/sort',
  artistWithId (id) {
    return `${this.artists}/${id}`
  },
  artistDuplicateCheck: '/artists/duplicate-check',
  albums: '/albums',
  albumWithId (id) {
    return `${this.albums}/${id}`
  },
  cardWithAlbumId (id) {
    return `${this.albums}/${id}/cards`
  },
  workingAlbums: '/albums/working',
  workingAlbumWithId (id) {
    return `${this.workingAlbums}/${id}`
  },
  albumSetState (id, state) {
    return `${this.workingAlbums}/${id}/${state}`
  },
  albumStateHistory (id) {
    return `${this.workingAlbums}/${id}/history`
  },
  cardWithWorkingAlbumId (id) {
    return `${this.workingAlbums}/${id}/cards`
  },
  batchState: '/batch/jobEnabled',
  batches: '/batch',
  batchExecute: '/batch/execute',
  nfcs: '/nfcCards',
  nfcWithId (id) {
    return `${this.nfcs}/${id}`
  },
  orders: '/productOrders',
  orderWithId (id) {
    return `${this.orders}/${id}`
  },
  banners: '/banners',
  bannerList: '/banners/search',
  bannerSort: '/banners/sort',
  bannerWithId (id) {
    return `${this.banners}/${id}`
  },
  questions: '/questions',
  addQuestions: '/user/questions',
  questionWithId (id) {
    return `${this.questions}/${id}`
  },
  notices: '/notices',
  noticeWithId (id) {
    return `${this.notices}/${id}`
  },
  policies: '/policies',
  policyWithId (id) {
    return `${this.policies}/${id}`
  },
  faqs: '/faqs',
  faqWithId (id) {
    return `${this.faqs}/${id}`
  },
  faqCategories: '/faqCategories',
  faqCategoryWithId (id) {
    return `${this.faqCategories}/${id}`
  },
  countries: '/countryCodes',
  countryWithId (id) {
    return `${this.countries}/${id}`
  },
  users: '/users',
  userWithId (id) {
    return `${this.users}/${id}`
  },
  userArtistsWithId (id) {
    return `${this.users}/${id}/artists`
  },
  userAlbumsWithId (id) {
    return `${this.users}/${id}/albums`
  },
  userCardsWithId (id) {
    return `${this.users}/${id}/cards`
  },
}
export default ApiList
