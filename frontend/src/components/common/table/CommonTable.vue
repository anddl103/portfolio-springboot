<template>
  <v-row
    class="ma-auto"
    justify="center"
  >
    <v-col cols="10">
      <v-data-table
        :calculate-widths="true"
        :headers="tableHeader"
        :items="tableData"
        :loading="loading"
        :hide-default-footer="hideFooter"
        :options.sync="options"
        :server-items-length="server ? totalRow : -1"
        :single-expand="true"
        :class="[(detail || popup) ? 'pointer' : '']"
        :item-class="itemRowClass"
        class="elevation-1"
        @click:row="clickRowHandler"
      >
        <template v-slot:top>
          <v-toolbar
            flat
          >
            <v-toolbar-title>{{ tableTitle }} 목록</v-toolbar-title>
            <v-spacer v-if="!isBatch" />
            <toolbar-batch-section
              v-if="isBatch"
              :batch-done.sync="copyOfBatchDone"
              class="display-contents"
            />
            <toolbar-keyword-search-section
              v-if="search"
              class="display-contents"
              :model.sync="keyword"
              :label="searchLabel"
            />
            <toolbar-filter-section
              v-if="isFiltering"
              class="display-contents"
              :model.sync="filter"
              :data="filterData"
              :title="filterTitle"
            />
            <toolbar-sort-button-section
              v-if="sortDialog"
              class="display-contents"
              :header="sortTableHeader"
              :data="sortTableData"
              @updateOrder="updateOrder"
            />
            <toolbar-create-button-section
              v-if="allowCreate && roleMatchCreate"
              class="display-contents"
              :label="tableTitle"
              @create="create"
            />
          </v-toolbar>
        </template>

        <!-- 아티스트 -->
        <template
          v-slot:item.artist="{ item }"
        >
          <v-row
            class="mx-auto my-3"
            align="center"
          >
            <v-col
              cols="12"
              class="pa-0 ma-auto text-align-center"
            >
              <v-avatar left>
                <v-img
                  class="my-2"
                  :src="artistGetUrl(item)"
                  :aspect-ratio="4/3"
                  max-width="70"
                  max-height="70"
                />
              </v-avatar>
            </v-col>
            <v-col
              cols="12"
              class="pa-0 ma-auto text-align-center"
            >
              <label class="v-label">{{ item.artist ? item.artist.name : item.name }}</label>
            </v-col>
          </v-row>
        </template>

        <!-- 썸네일 -->
        <template
          v-slot:item.thumbnailUrl="{ item }"
        >
          <v-img
            class="my-2"
            :src="getSignedUrl(item.thumbnailUrl)"
            :aspect-ratio="4/3"
            max-width="70"
            max-height="70"
          />
        </template>

        <!-- 컨텐츠.썸네일 -->
        <template
          v-if="thumbnailInContent"
          v-slot:item.contents.thumbnailUrl="{ item }"
        >
          <v-img
            class="my-2"
            :src="getSignedUrl(item.contents.thumbnailUrl)"
            :aspect-ratio="4/3"
            max-width="70"
            max-height="70"
          />
        </template>

        <!-- 서치태그 -->
        <template
          v-slot:item.tags="{ item }"
        >
          {{ formatTags(item.tags) }}
        </template>

        <!-- 앨범 -->
        <template
          v-slot:item.album="{ item }"
        >
          <v-row
            class="mx-auto my-3"
            align="center"
          >
            <v-col
              cols="12"
              class="pa-0 ma-auto text-align-center"
            >
              <v-avatar left>
                <v-img
                  class="my-2"
                  :src="albumGetUrl(item)"
                  :aspect-ratio="4/3"
                  max-width="70"
                  max-height="70"
                />
              </v-avatar>
            </v-col>
            <v-col
              cols="12"
              class="pa-0 ma-auto text-align-center"
            >
              <label class="v-label">{{ albumGetTitle(item) }}</label>
            </v-col>
          </v-row>
        </template>

        <!-- 앨범.썸네일 -->
        <template
          v-slot:item.albumThumbnail="{ item }"
        >
          <v-img
            class="my-2"
            :src="getSignedUrl(item.thumbnailKey)"
            :aspect-ratio="4/3"
            max-width="70"
            max-height="70"
          />
        </template>

        <!-- 앨범 제목 -->
        <template
          v-slot:item.albumTitle="{ item }"
        >
          {{ item.titleKr }}
        </template>

        <!-- 앨범 상태 -->
        <template
          v-slot:item.state="{ item }"
        >
          {{ handleShowEnum(item.state) }}
        </template>

        <!-- 카드 썸네일 & 서치태그 -->
        <template
          v-slot:item.cards="{ item }"
        >
          <v-row
            v-for="(card, i) in item.cards"
            :key="i"
            class="ma-auto"
            align="center"
          >
            <v-avatar left>
              <v-img
                :src="getSignedUrl(card.thumbnailKey)"
                max-width="70"
                max-height="70"
              />
            </v-avatar>
            <v-divider
              class="mx-4"
              inset
              vertical
            />
            {{ formatTags(card.tags) }}
          </v-row>
        </template>

        <!-- 주문량 -->
        <template
          v-slot:item.orderQuantity="{ item }"
        >
          {{ item.verifiedCount }} / {{ item.quantity }}
        </template>

        <!-- 정렬순서 -->
        <template
          v-slot:item.sortOrder="{ item }"
        >
          {{ item.sortOrder > 0 ? item.sortOrder : '' }}
        </template>

        <!-- 공개 여부 -->
        <template
          v-slot:item.display="{ item }"
        >
          {{ item.display ? '공개' : '비공개' }}
        </template>

        <!-- 공지일시 -->
        <template
          v-slot:item.noticeAt="{ item }"
        >
          {{ moment.utc(item.noticeAt).tz('Asia/Seoul').format('YYYY-MM-DD | HH : mm : ss') }}
        </template>

        <!-- 생성일시 -->
        <template
          v-slot:item.createdAt="{ item }"
        >
          {{ moment.utc(item.createdAt).tz('Asia/Seoul').format('YYYY-MM-DD | HH : mm : ss') }}
        </template>

        <!-- 정책: 사용처 -->
        <template
          v-slot:item.usage="{ item }"
        >
          {{ item.usage.description }}
        </template>

        <!-- 유저: uid -->
        <template
          v-slot:item.uid="{ item }"
        >
          <p
            class="my-auto"
            :class="item.withdrawal.flag ? 'quit' : ''"
          >
            {{ item.uid }}
          </p>
        </template>

        <!-- 유저: 이메일 -->
        <template
          v-slot:item.userEmail="{ item }"
        >
          {{ handleUserEmail(item) }}
        </template>

        <!-- 유저: 가입(탈퇴)일시 -->
        <template
          v-slot:item.datetime="{ item }"
        >
          {{ moment.utc(handleJoinDatetime(item)).tz('Asia/Seoul').format('YYYY-MM-DD | HH : mm : ss') }}
        </template>

        <!-- 배너: 앨범 -->
        <template
          v-slot:item.bannerAlbum="{ item }"
        >
          <v-row
            class="mx-auto my-3"
            align="center"
          >
            <v-col
              cols="12"
              class="pa-0 ma-auto text-align-center"
            >
              <v-avatar left>
                <v-img
                  class="my-2"
                  :src="getSignedUrl(item.contents.ko.image === '' ? item.contents.en.image : item.contents.ko.image)"
                  :aspect-ratio="4/3"
                  max-width="70"
                  max-height="70"
                />
              </v-avatar>
            </v-col>
            <v-col
              cols="12"
              class="pa-0 ma-auto text-align-center"
            >
              <label class="v-label">{{ item.titleKr }}</label>
            </v-col>
          </v-row>
        </template>

        <!-- 배치: 앨범 -->
        <template
          v-slot:item.batchAlbum="{ item }"
        >
          <v-row
            class="mx-auto my-3"
            align="center"
          >
            <v-avatar left>
              <v-img
                class="my-2"
                :src="getSignedUrl(item.albumThumbnailKey)"
                :aspect-ratio="4/3"
                max-width="70"
                max-height="70"
              />
            </v-avatar>
            <v-divider
              class="mx-4"
              inset
              vertical
            />
            {{ item.title }}
          </v-row>
        </template>

        <!-- 배치: 갱신일시 -->
        <template
          v-slot:item.updatedAt="{ item }"
        >
          {{ moment.utc(item.updatedAt).tz('Asia/Seoul').format('YYYY-MM-DD | HH : mm : ss') }}
        </template>

        <!-- Faq: 카테고리 -->
        <template
          v-slot:item.category="{ item }"
        >
          {{ getCategoryInfo(item.category) }}
        </template>

        <!-- Faq: 언어 -->
        <template
          v-slot:item.localeCode="{}"
        >
          <v-row
            v-for="lc in localeCodes"
            :key="lc.id"
            class="my-3 mx-0"
          >
            <v-col
              class="pa-0"
            >
              {{ lc.comment }}
            </v-col>
          </v-row>
        </template>

        <!-- Faq, Policy, Notice : 타이틀 -->
        <template
          v-slot:item.title="{ item }"
        >
          <v-row
            v-for="lc in localeCodes"
            :key="lc.id"
            class="my-3 mx-0"
          >
            <v-col
              class="pa-0"
            >
              {{ item.localeCodeContents[lc.code].subject }}
            </v-col>
          </v-row>
        </template>

        <!-- Question : 타이틀 -->
        <template
          v-slot:item.questionTitle="{ item }"
        >
          <v-row
            class="my-3 mx-0"
          >
            <v-col
              class="pa-0"
            >
              {{ item.title }}
            </v-col>
          </v-row>
        </template>

        <!-- actions -->
        <template
          v-slot:item.actions="{ item }"
        >
          <v-row
            v-if="handleShowDeleteBtn(item)"
            id="actions"
            class="mx-1"
            justify="end"
          >
            <v-btn
              tile
              plain
              text
              small
              outlined
              @click.stop="openDeleteDialog(item)"
            >
              삭제
            </v-btn>
          </v-row>
        </template>
      </v-data-table>
      <common-del-dialog
        :open.sync="deleteDialog"
        :target="delName"
        :url="getDeleteUrl(target.id)"
        @confirm="syncDataAfterRemove"
      />
    </v-col>
  </v-row>
</template>

<script>
  import tagFormatMixin from '@/mixins/tagFormatMixin'
  import CommonDelDialog from '@/components/common/CommonDelDialog'
  import store from '@/store'
  import { albumState, batchState, role } from '@/assets/enums'
  import ToolbarBatchSection from '@/components/common/table/ToolbarBatchSection'
  import ToolbarKeywordSearchSection from '@/components/common/table/ToolbarKeywordSearchSection'
  import ToolbarFilterSection from '@/components/common/table/ToolbarFilterSection'
  import ToolbarCreateButtonSection from '@/components/common/table/ToolbarCreateButtonSection'
  import ToolbarSortButtonSection from '@/components/common/table/ToolbarSortButtonSection'
  import s3GetUrlMixin from '@/mixins/s3GetUrlMixin'

  export default {
    name: 'CommonTable',
    components: {
      CommonDelDialog,
      ToolbarBatchSection,
      ToolbarKeywordSearchSection,
      ToolbarFilterSection,
      ToolbarCreateButtonSection,
      ToolbarSortButtonSection,
    },
    mixins: [
      tagFormatMixin,
      s3GetUrlMixin,
    ],
    props: {
      deleteUrl: { type: String, default: '' },
      sortDialog: { type: Boolean, default: false },
      allowDelete: { type: Boolean, default: true },
      allowCreate: { type: Boolean, default: true },
      roleMatchCreate: { type: Boolean, default: true },
      modify: { type: Boolean, default: true },
      detail: { type: Boolean, default: false },
      popup: { type: Boolean, default: false },
      totalRow: Number,
      server: { type: Boolean, default: false },
      search: { type: Boolean, default: false },
      searchLabel: { type: String, default: '태그' },
      thumbnail: { type: Boolean, default: false },
      thumbnailInContent: { type: Boolean, default: false },
      hideFooter: { type: Boolean, default: false },
      loading: Boolean,
      tableTitle: String,
      tableData: Array,
      tableHeader: Array,
      isToolbarButtonToggle: { type: Boolean, default: false },
      isBatch: { type: Boolean, default: false },
      isFiltering: { type: Boolean, default: false },
      filterData: Array,
      filterTitle: String,
      isWorkingAlbum: { type: Boolean, default: false },
      batchDone: { type: Boolean, default: false },
    },
    data: () => ({
      keyword: '',
      options: {},
      sortDialogHandler: false,
      deleteDialog: false,
      target: {},
      filter: '',
      categories: store.state.common.faqCategories,
      localeCodes: store.state.common.localeCodes,
    }),
    computed: {
      copyOfBatchDone: {
        get () {
          return this.batchDone === undefined ? false : JSON.parse(JSON.stringify(this.batchDone))
        },
        set (val) {
          this.$emit('update:batchDone', val)
        },
      },
      batchStateText () {
        return this.tableTitle.concat(' ', this.toolbarButtonState.description)
      },
      toolbarButtonState () {
        return store.state.common.batchState
      },
      toolbarButtonText () {
        let text = ''

        if (this.isToolbarButtonToggle) {
          switch (this.toolbarButtonState.code) {
            case batchState.OFF.code:
              text = this.tableTitle + ' 실행'
              break
            case batchState.WORKING.code:
            case batchState.ON.code:
              text = this.tableTitle + ' 정지'
              break
            default:
              text = 'ERROR'
          }
        } else {
          text = this.tableTitle + ' 추가'
        }
        return text
      },
      delName () {
        if (this.target.tags) {
          return this.formatTags(this.target.tags)
        }
        if (this.target.name) {
          return this.target.name
        }
        if (this.target.comment) {
          return this.target.comment
        }
        if (this.target.title) {
          return this.target.title
        }
        if (this.target.localeCodeContents && this.target.id) {
          return this.target.id
        }
        return ''
      },
      sortTableHeader () {
        const arr = this.tableHeader.filter(th => th.sortTableHeader)
        const dragger = {
          text: '',
          value: 'dragger',
          align: 'right',
          sortable: false,
        }
        const rowNum = {
          text: '순서',
          value: 'rowNum',
          width: 100,
          sortable: false,
        }
        arr.splice(0, 0, rowNum)
        arr.push(dragger)
        return arr
      },
      sortTableData () {
        return this.tableData.filter(td => td.display)
      },
    },
    watch: {
      keyword () {
        this.$emit('watchKeyword', this.keyword)
      },
      options () {
        this.$emit('watchOptions', this.options)
      },
      filter () {
        this.$emit('watchFilter', this.filter)
      },
    },
    methods: {
      handleShowDeleteBtn (item) {
        const con1 = this.allowDelete === true
        let con2 = true
        if (item.state !== undefined) {
          if (item.deployedFlag) {
            con2 = false
          }
        }
        return con1 && con2
      },
      getCategoryInfo (val) {
        const target = this.categories.find(o => o.id === val)
        if (target) return target.comment
        return ''
      },
      albumGetTitle (item) {
        let title
        if (this.isBatch) title = item.title
        else title = item.album ? item.album.title : item.titleKr
        return title
      },
      albumGetUrl (item) {
        let key
        if (this.isBatch) {
          key = item.albumThumbnailKey
        } else {
          key = item.album === undefined ? item.thumbnailKey : item.album.thumbnailKey
        }
        return this.getSignedUrl(key, this.isWorkingAlbum)
      },
      artistGetUrl (item) {
        const key = item.artist === undefined ? item.logoKey : item.artist.logoKey
        return this.getSignedUrl(key)
      },
      handleUserEmail (item) {
        return item.withdrawal.flag ? '탈퇴한 회원' : item.email
      },
      handleJoinDatetime (item) {
        return item.withdrawal.flag ? item.withdrawal.updatedAt : item.createdAt
      },
      getDeleteUrl (id) {
        return this.deleteUrl + '/' + id
      },
      updateOrder (arr) {
        this.$emit('syncOrder', arr)
      },
      itemRowClass (item) {
        if (!this.isWorkingAlbum) return
        const stateCode = item.state.code
        const targetGroup = []
        if (store.state.auth.currentRole === role.CONTENTS_EDITOR) {
          targetGroup.push(...[albumState.WORKING, albumState.REJECTED])
        }
        if (store.state.auth.currentRole === role.CONTENTS_MANAGER) {
          targetGroup.push(...[albumState.SUBMITTED, albumState.REVIEWING])
        }

        return targetGroup.includes(stateCode) ? 'has-mission' : ''
      },
      clickRowHandler (item) {
        if (this.showExpand) {
          this.getExpand(item)
        } else if (this.detail) {
          this.getDetail(item)
        } else {
          this.edit(item)
        }
      },
      getExpand (item) {
        const idx = this.expanded.findIndex(it => it === item)
        if (idx >= 0) {
          this.expanded.splice(idx, 1)
        } else {
          // for single expand. multi로 바꾸기 위해서는 v-data-table 의 single-expand 를 false 로 해 줄 필요가 있다.
          this.expanded.splice(idx, 1, item)

          // for multi expand
          // this.expanded.splice(0, 0, item)
        }
      },
      getDetail (item) {
        this.$emit('detail', item)
      },
      create () {
        if (this.isToolbarButtonToggle) {
          this.$emit('toggle')
        } else {
          this.$emit('create')
        }
      },
      edit (item) {
        this.$emit('edit', item)
      },
      syncDataAfterRemove () {
        this.target = {}
        this.$emit('delete')
      },
      openDeleteDialog (item) {
        this.target = item
        this.deleteDialog = true
      },
      handleShowEnum (target) {
        if (target === null) return ''
        return target.description
      },
    },
  }
</script>
<style>
.quit {
  text-decoration: white double line-through;
}
.display-contents {
  display: contents;
}
.has-mission {
  background-color: lightslategrey;
}
</style>
