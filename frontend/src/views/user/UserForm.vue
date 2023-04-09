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

    <v-row class="ma-auto">
      <v-col
        cols="2"
      >
        기본 정보
      </v-col>
      <v-col cols="8">
        <v-card>
          <v-data-table
            hide-default-footer
            hide-default-header
            :items="tableData"
          >
            <template v-slot:body="item">
              <tr
                v-for="(it, i) of item.items.filter(obj => obj.aliveOnly === undefined || obj.aliveOnly !== targetItem.withdrawal.flag)"
                :key="i"
              >
                <td class="pa-3 header">
                  {{ it.header }}
                </td>
                <td v-if="typeof it.contents === 'object'">
                  <v-row
                    v-for="(e, j) in Object.entries(it.contents)"
                    :key="j"
                    class="ma-0 pa-0"
                  >
                    <v-col
                      class="ma-0 py-1 px-3 text-line-break"
                      align-self="center"
                    >
                      {{ e[0] }}
                    </v-col>
                    <v-col
                      class="ma-0 pa-0"
                      align-self="center"
                    >
                      {{ e[1] }}
                    </v-col>
                  </v-row>
                </td>
                <td v-else>
                  <v-row class="ma-0 py-1 px-3">
                    {{ it.contents }}
                  </v-row>
                </td>
              </tr>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>
    <v-row
      class="ma-auto"
    >
      <v-col
        cols="2"
      >
        보유 카드
      </v-col>
      <v-col cols="8">
        <v-card>
          <v-autocomplete
            v-model="selectedArtist"
            :items="artists"
            clearable
            item-text="name"
            return-object
            label="아티스트 필터"
          />
          <v-autocomplete
            v-model="selectedAlbum"
            :items="filteredAlbums"
            :disabled="blockSelectAlbum"
            clearable
            item-text="title"
            return-object
            label="앨범 필터"
          />
          <v-card
            class="width-webkit-fill"
            outlined
            color="#444444"
          >
            <v-card-text>
              <v-row>
                <v-col
                  v-for="card in cards"
                  :key="card.id"
                  :cols="previewCols"
                  class="card-grid"
                >
                  <v-img
                    :src="card.contents.thumbnailUrl"
                    contain
                    width="170"
                    height="220"
                  />
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-card>
      </v-col>
    </v-row>
    <v-divider
      class="mt-3 mb-10"
    />
    <v-row
      class="ma-auto"
      justify="center"
    >
      <v-btn
        @click="toList"
      >
        목록
      </v-btn>
    </v-row>
  </v-container>
</template>
<script >
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import router from '@/router'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import tagFormatMixin from '@/mixins/tagFormatMixin'
  import moment from 'moment'
  import commonFormMixin from '@/mixins/commonFormMixin'
  import store from '@/store'

  export default {
    name: 'UserForm',
    components: {
      CommonErrorAlert,
    },
    mixins: [
      commonErrorMixin,
      tagFormatMixin,
      commonFormMixin,
    ],
    data: () => ({
      targetItem: JSON.parse(JSON.stringify(store.state.common.targetItem)),
      names: null,
      artists: [],
      albums: [],
      filteredAlbums: [],
      members: [],
      cards: [],
      selectedArtist: null,
      selectedAlbum: {},
      selectedMember: null,
      blockSelectAlbum: true,
      previewCols: 4,
      policies: [],
      deviceInfos: [],
    }),
    computed: {
      tableData () {
        const user = this.targetItem
        const arr = [
          {
            header: 'uid',
            contents: user.uid,
          },
          {
            header: 'email',
            contents: user.withdrawal.flag ? '탈퇴한 회원' : user.email,
          },
          {
            header: '계정 연동 정보',
            contents: this.handleSNS(user.providers),
            aliveOnly: true,
          },
          {
            header: '약관',
            contents: {
              '개인 정보 수집 이용 약관': this.handleFlag(user.signupAccountAgree.privacyPolicy),
              '서비스 이용 약관': this.handleFlag(user.signupAccountAgree.termsOfService),
              '푸쉬 알림 약관': this.handleFlag(user.signupAccountAgree.push),
              '탈퇴 안내': this.handleFlag(user.withdrawal),
            },
            aliveOnly: true,
          },
          {
            header: '사용 기기 토큰',
            contents: user.deviceTokens,
            aliveOnly: true,
          },
        ]

        return arr
      },
    },
    watch: {
      selectedArtist () {
        this.toggleFilterSelection()
        this.handleAlbumFilter()
        this.getUserCardsInfo(this.targetItem.id)
      },
      selectedAlbum () {
        this.getUserCardsInfo(this.targetItem.id)
      },
    },
    mounted () {
      this.initialize()
    },
    methods: {
      initialize () {
        this.setInitItems()
      },
      toggleFilterSelection () {
        this.blockSelectAlbum = this.selectedArtist === null
      },
      handleAlbumFilter () {
        if (this.blockSelectAlbum === true) {
          this.selectedAlbum = {}
        } else {
          this.selectedAlbum = {}
          this.filteredAlbums = this.albums.filter(al => al.artistId === this.selectedArtist.artistId)
        }
      },
      handleFlag (obj) {
        if (obj === undefined) return
        const agree = '동의'
        const disagree = '미동의'
        let str = obj.flag ? agree : disagree
        if (str === agree) str = str.concat(' : ').concat(moment(obj.updatedAt).format('YYYY-MM-DD | hh:mm'))
        return str
      },
      handleSNS (arr) {
        if (arr === undefined || arr === null) return ''
        return arr.join(', ').replaceAll('password', 'email')
      },
      getUserArtistsInfo (targetId) {
        axios.get(ApiList.userArtistsWithId(targetId)).then(res => {
          if (!this.validateResponse(res)) return
          this.artists = res.data.data
        }).catch(err => {
          this.setError(err.message)
        })
      },
      getUserAlbumsInfo (targetId) {
        axios.get(ApiList.userAlbumsWithId(targetId)).then(res => {
          if (!this.validateResponse(res)) return
          this.albums = res.data.data
        }).catch(err => {
          this.setError(err.message)
        })
      },
      getUserCardsInfo (targetId) {
        const params = {
          page: 0,
          pageSize: -1,
        }
        if (this.selectedArtist !== null) {
          params.artistId = this.selectedArtist.artistId
        }
        if (this.selectedAlbum !== null) {
          params.albumId = this.selectedAlbum.albumId
        }
        axios.get(ApiList.userCardsWithId(targetId), { params }).then(res => {
          if (!this.validateResponse(res)) return
          this.cards = res.data.data
        }).catch(err => {
          this.setError(err.message)
        })
      },
      setInitItems () {
        const targetId = this.targetItem.id
        this.getUserArtistsInfo(targetId)
        this.getUserAlbumsInfo(targetId)
        this.getUserCardsInfo(targetId)
      },
      async toList () {
        await router.push({ name: 'User' })
      },
    },
  }
</script>
<style>
.header {
  width: 200px
}
.card-grid {
  border: 1px solid #EEEEEE;
  display: inline-block;
  text-align: -webkit-center;
}
.text-line-break {
  max-width: 500px;
  white-space: break-spaces;
  word-break: break-all;
}
tr:nth-of-type(odd) {
  background-color: rgba(100, 100, 100, .2);
}
</style>
