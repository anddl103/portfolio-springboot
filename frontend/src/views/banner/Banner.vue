<template>
  <v-container
    class="pa-4"
    fluid
  >
    <common-error-alert
      :error="error"
      :error-msg="errorMsg"
      @close="resetError"
    />

    <common-table
      :keyword="keyword"
      :table-data="tableData"
      :table-header="tableHeader"
      table-title="배너"
      :thumbnail="true"
      :detail="true"
      :modify="false"
      :loading="loading"
      :server="true"
      :search="true"
      :total-row="totalRow"
      :sort-dialog="true"
      :delete-url="deleteUrl"
      :is-filtering="true"
      :filter-data="artists"
      :filter-title="'아티스트'"
      :role-match-create="checkRole()"
      @detail="toBannerDetail"
      @create="toCreateBanner"
      @delete="deleteItem"
      @watchOptions="watchOptions"
      @watchKeyword="watchKeyword"
      @watchFilter="watchFilter"
      @syncOrder="syncOrder"
    />
  </v-container>
</template>
<script>
  import axios from '@/axios/index'
  import ApiList from '@/axios/api-list'
  import router, { routeName } from '@/router'
  import dataTableMixin from '@/mixins/dataTableMixin'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import CommonTable from '@/components/common/table/CommonTable'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'
  import tagFormatMixin from '@/mixins/tagFormatMixin'
  import store from '@/store'
  import { role } from '@/assets/enums'

  export default {
    name: 'Banner',
    components: {
      CommonTable,
      CommonErrorAlert,
    },
    mixins: [
      dataTableMixin,
      commonErrorMixin,
      calcOffsetMixin,
      tagFormatMixin,
    ],
    data: () => ({
      deleteUrl: ApiList.banners,
      editedIndex: -1,
      editedItem: {},
      defaultItem: {},
      keyword: '',
      cardDeleteDialog: false,
      deleteTarget: {},
      tableData: [],
      artists: [],
      options: {
        page: 1,
        itemsPerPage: 10,
      },
      tableHeader: [
        {
          text: '아티스트',
          value: 'artist',
          sortable: false,
          sortTableHeader: true,
          align: 'center',
        },
        {
          text: '앨범',
          value: 'bannerAlbum',
          sortable: false,
          sortTableHeader: true,
          align: 'center',
        },
        {
          text: '태그',
          value: 'tags',
          sortable: false,
          sortTableHeader: true,
        },
        {
          text: '공개 여부',
          value: 'display',
          sortable: false,
        },
        {
          text: '표시순서',
          value: 'sortOrder',
          sortable: false,
        },
        {
          text: '',
          value: 'actions',
          sortable: false,
        },
      ],
      totalRow: 0,
      listingTableData: [],
    }),
    watch: {
      options: {
        handler () {
          this.getTableData()
        },
        deep: true,
      },
      targetFilter: {
        handler () {
          this.getTableData()
        },
        deep: true,
      },
    },
    mounted () {
      this.getArtists()
    },
    methods: {
      checkRole () {
        const currentRole = store.state.auth.currentRole
        const creatableRoles = [role.CONTENTS_EDITOR]

        return creatableRoles.includes(currentRole)
      },
      syncOrder (arr) {
        const orderList = []
        for (const data of arr) {
          const rd = {
            bannerId: data.id,
            sortOrder: data.sortOrder,
          }
          orderList.push(rd)
        }
        const request = {
          orderList: orderList,
        }
        axios.patch(ApiList.bannerSort, request).then(async res => {
          if (!await this.validateResponse(res)) return
          await this.getTableData()
        }).catch((err) => {
          this.setError(err)
        })
      },
      async toBannerDetail (item) {
        const idx = this.tableData.indexOf(item)
        store.commit('setTargetIndex', idx)
        await axios.get(ApiList.bannerWithId(item.id)).then(res => {
          store.commit('setTargetItem', res.data.data)
        }).catch(err => {
          this.setError(err)
        })
        await router.push({ name: routeName.BANNER_FORM })
      },
      toCreateBanner () {
        store.commit('resetTargetItem')
        router.push({ name: routeName.BANNER_FORM })
      },
      async getTableData () {
        const { itemsPerPage } = this.options
        const params = this.getOffsetParams(itemsPerPage)
        if (this.keyword !== '') params.keyword = this.keyword
        if (this.targetFilter !== '') params.artistId = this.targetFilter

        axios.get(ApiList.bannerList, { params }).then(res => {
          res.data.data.sort((a, b) => a.sortOrder - b.sortOrder)
          res.data.data.sort((a, b) => b.display - a.display)

          this.tableData = res.data.data
          this.totalRow = res.data.pageInfo.totalElements
        }).catch(err => {
          this.setError(err)
        })
      },
      async getArtists () {
        const params = this.getParamsForAll()
        axios.get(ApiList.artists, { params }).then(res => {
          this.artists = res.data.data
        }).catch((err) => {
          this.setError(err)
        })
      },

    },
  }
</script>
