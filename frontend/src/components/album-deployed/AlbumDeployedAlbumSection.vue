<template>
  <v-card
    class="ma-auto"
  >
    <v-row class="ma-auto">
      <v-col
        cols="2"
      >
        앨범 정보
      </v-col>
    </v-row>
    <v-row
      class="ma-auto"
      justify="center"
      align="center"
    >
      <v-row
        class="ma-3 bg-color"
        align="center"
      >
        <v-col
          cols="12"
        >
          <label class="v-label theme--dark">아티스트</label>
          <br>
          <v-avatar
            left
            class="my-1"
          >
            <v-img
              :src="getSignedUrl(artist.logoKey)"
              max-width="70"
              max-height="70"
            />
          </v-avatar>
          <v-divider
            class="mx-4"
            vertical
            style="display: inline;"
          />
          {{ artist.name }}
        </v-col>
      </v-row>
      <v-row
        class="ma-auto width-webkit-fill bg-color"
      >
        <v-col
          cols="12"
        >
          <v-data-table
            :headers="languageTitleDescTableHeader"
            :items="tableData"
            hide-default-footer
            disable-sort
          >
            <!-- 배치: 언어 -->
            <template
              v-slot:item.language="{ item }"
            >
              <v-row
                class="my-3 mx-0"
              >
                <v-col
                  class="pa-0"
                >
                  <label>{{ item.language.comment }}</label>
                </v-col>
              </v-row>
            </template>
          </v-data-table>
        </v-col>
      </v-row>
      <v-col cols="12">
        <v-row class="ma-auto bg-color">
          <v-col cols="6">
            <table class="ma-3">
              <tr>
                <td>
                  <label class="v-label mr-3">
                    VERSION
                  </label>
                </td>
                <td>
                  <label class="v-label theme--dark ml-3">{{ album.version }}</label>
                </td>
              </tr>
              <tr>
                <td>
                  <label class="v-label">
                    배포일시
                  </label>
                </td>
                <td>
                  <label class="v-label theme--dark ml-3">{{ moment.utc(album.createdAt).tz('Asia/Seoul').format('YYYY-MM-DD | HH : mm : ss') }}</label>
                </td>
              </tr>
              <tr>
                <td>
                  <label class="v-label">
                    컨텐츠 타입
                  </label>
                </td>
                <td>
                  <label class="v-label theme--dark ml-3">{{ album.reward.type.description }}</label>
                </td>
              </tr>
            </table>
          </v-col>
          <v-col
            class="ma-auto"
            cols="6"
          >
            <v-col cols="12">
              <label class="v-label theme--dark">검색용 태그</label>
              <v-chip-group>
                <v-chip
                  v-for="tag in album.tags"
                  :key="tag"
                >
                  {{ tag }}
                </v-chip>
              </v-chip-group>
            </v-col>
          </v-col>
        </v-row>
      </v-col>
      <v-row
        class="bg-color"
        justify="center"
        align="center"
      >
        <file-upload
          v-for="obj in uploadedFiles.filter(uf => uf.reward === undefined)"
          :ref="obj.refname"
          :key="obj.refname"
          class="ma-3"
          :file-info="obj"
          :width="obj.isVideo ? 300 : 150"
          :height="150"
          readonly
          :valid="obj.isValid"
        />
        <v-divider
          vertical
          class="ma-3"
        />
        <file-upload
          v-for="obj in uploadedFiles.filter(uf => uf.reward)"
          :ref="obj.refname"
          :key="obj.refname"
          class="ma-3"
          :file-info="obj"
          :width="obj.isVideo ? 300 : 150"
          :height="150"
          readonly
          :valid="obj.isValid"
        />
      </v-row>
    </v-row>
  </v-card>
</template>
<script>
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import store from '@/store'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import commonAddLanguageTableMixin from '@/mixins/commonAddLanguageTableMixin'
  import FileUpload from '@/components/common/FileUpload'
  import fileUploadMixin from '@/mixins/fileUploadMixin'
  import {
    ruleType,
    uploadType,
    common,
  } from '@/assets/enums'
  import s3GetUrlMixin from '@/mixins/s3GetUrlMixin'
  import albumMultiLanguageTableMixin from '@/mixins/albumMultiLanguageTableMixin'
  const constant = Object.freeze({
    tableId: {
      HEADER: {
        id: 'header',
        text: '',
      },
      TITLE: {
        id: 'title',
        text: '앨범 타이틀',
      },
      DESCRIPTION: {
        id: 'description',
        text: '앨범 설명',
      },
      REWARD_TITLE: {
        id: 'rewardTitle',
        text: '리워드 타이틀',
      },
    },
    slotText: {
      ALBUM_THUMBNAIL: '앨범 썸네일',
      REWARD_THUMBNAIL: '리워드 썸네일',
      REWARD_IMAGE: '리워드 이미지',
      REWARD_VIDEO: '리워드 비디오',
    },
  })

  export default {
    name: 'AlbumDeployedAlbumSection',
    components: {
      FileUpload,
    },
    mixins: [
      commonErrorMixin,
      commonAddLanguageTableMixin,
      albumMultiLanguageTableMixin,
      fileUploadMixin,
      s3GetUrlMixin,
    ],
    props: {
      album: Object,
      artist: Object,
      isWorkingAlbum: Boolean,
    },
    data: () => ({
      contentsType: [],
      uploadedFiles: [],
      tags: [],
      formValid: true,
      selectedArtist: null,
      targetItem: store.state.common.targetItem,
    }),
    mounted () {
      this.initialize()
    },
    methods: {
      async initialize () {
        await this.createLanguageTableData()
        await this.setUploadedFiles()
        await this.getContentsTypes()
      },
      async getContentsTypes () {
        axios.get(ApiList.contentsTypes).then(res => {
          if (!this.validateResponse(res)) return
          this.contentsType = res.data.data
        })
        // .catch(err => this.setError(err))
      },
      async createLanguageTableData () {
        const titleData = store.state.common.targetIndex < 0 ? [] : [this.album.title]
        const descriptionData = store.state.common.targetIndex < 0 ? [] : [this.album.description]
        const rewardTitleData = store.state.common.targetIndex < 0 ? [] : [this.album.reward.title]
        this.tables = [
          {
            id: constant.tableId.HEADER.id,
            title: constant.tableId.HEADER.text,
            data: undefined,
            asHeader: true,
          },
          {
            id: constant.tableId.TITLE.id,
            title: constant.tableId.TITLE.text,
            data: titleData,
          },
          {
            id: constant.tableId.DESCRIPTION.id,
            title: constant.tableId.DESCRIPTION.text,
            data: descriptionData,
          },
          {
            id: constant.tableId.REWARD_TITLE.id,
            title: constant.tableId.REWARD_TITLE.text,
            data: rewardTitleData,
          },
        ]
      },
      async setUploadedFiles () {
        this.uploadedFiles = [
          {
            refname: uploadType.THUMBNAIL,
            model: null,
            accept: this.imageAccept,
            slotText: constant.slotText.ALBUM_THUMBNAIL,
            key: this.album.thumbnailKey === undefined ? common.EMPTY : this.album.thumbnailKey,
            isKey: false,
            isVideo: false,
            isValid: true,
            rules: [ruleType.REQUIRED, ruleType.IMAGE],
            dimension: {
              x: 0,
              y: 0,
            },
            isWorkingAlbum: this.isWorkingAlbum,
          },
          {
            refname: uploadType.REWARD_THUMBNAIL,
            model: null,
            accept: this.imageAccept,
            slotText: constant.slotText.REWARD_THUMBNAIL,
            key: this.album.reward === undefined ? common.EMPTY : this.album.reward.thumbnailKey === undefined ? common.EMPTY : this.album.reward.thumbnailKey,
            isKey: false,
            isVideo: false,
            isValid: true,
            rules: [ruleType.REQUIRED, ruleType.IMAGE],
            reward: true,
            dimension: {
              x: 0,
              y: 0,
            },
            isWorkingAlbum: this.isWorkingAlbum,
          },
          {
            refname: uploadType.REWARD_IMAGE,
            model: null,
            accept: this.imageAccept,
            slotText: constant.slotText.REWARD_IMAGE,
            isKey: true,
            key: this.album.reward === undefined ? common.EMPTY : this.album.reward.imageKey === undefined ? common.EMPTY : this.album.reward.imageKey,
            isVideo: false,
            isValid: true,
            reward: true,
            rules: [ruleType.IMAGE],
            dimension: {
              x: 0,
              y: 0,
            },
            isWorkingAlbum: this.isWorkingAlbum,
          },
          {
            refname: uploadType.REWARD_VIDEO,
            model: null,
            accept: this.videoAccept,
            slotText: constant.slotText.REWARD_VIDEO,
            isKey: true,
            key: this.album.reward === undefined ? common.EMPTY : this.album.reward.videoKey === undefined ? common.EMPTY : this.album.reward.videoKey,
            isVideo: true,
            isValid: true,
            reward: true,
            rules: [ruleType.VIDEO],
            dimension: {
              x: 0,
              y: 0,
            },
            isWorkingAlbum: this.isWorkingAlbum,
          },
        ]
      },
    },
  }
</script>
<style scoped>
.bg-color:nth-child(odd) {
  background-color : #333333 !important
}
</style>
