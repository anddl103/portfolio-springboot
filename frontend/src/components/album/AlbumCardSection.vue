<template>
  <v-card>
    <v-row
      class="ma-auto align-center"
    >
      <v-col>
        카드 정보
      </v-col>
      <v-col
        class="text-align-right"
      >
        <v-btn
          v-if="editMode"
          @click="addCard"
        >
          카드 추가
        </v-btn>
      </v-col>
    </v-row>
    <v-card-text
      v-if="cards.length > 0"
      class="ma-auto"
      justify="center"
      align="center"
    >
      <v-data-table
        :items="cardsNotDeleted"
        :headers="cardHeader"
        hide-default-header
        hide-default-footer
      >
        <template v-slot:body="{ items, headers }">
          <tr class="table-header">
            <td
              v-for="(header, i) in headers"
              :key="i"
              class="text-align-center"
            >
              {{ header.text }}
            </td>
          </tr>
          <draggable
            :list="items"
            direction="h"
            tag="tbody"
            class="width-webkit-fill"
            v-bind="{disabled : !editMode}"
            @end="sortCards(items)"
          >
            <tr
              v-for="(item, k) in items"
              :key="k"
              class="table-row-height"
              @click.prevent="editMode ? cardEdit(k, item) : ''"
            >
              <td
                v-for="(header, j) in headers"
                :key="j"
                class="text-align-center"
              >
                <v-icon
                  v-if="header.value === 'order'"
                  :disabled="!editMode"
                >
                  mdi-arrow-all
                </v-icon>
                <v-spacer v-if="header.value === 'actions'" />
                <v-btn
                  v-if="header.value === 'actions' && editMode"
                  color="primary"
                  dark
                  class="ma-auto"
                  fab
                  x-small
                  @click.stop="removeRow(item)"
                >
                  <v-icon>
                    mdi-minus
                  </v-icon>
                </v-btn>
                <v-row
                  v-if="header.value === 'position'"
                  class="ma-auto"
                  justify="center"
                >
                  {{ item.position }}
                </v-row>
                <v-row
                  v-if="header.value === 'type'"
                  class="ma-auto"
                  justify="center"
                >
                  {{ item.contents.type.description }}
                </v-row>
                <v-row
                  v-if="header.value === 'members'"
                  class="ma-auto"
                  justify="center"
                >
                  {{ handleMemberName(item.members) }}
                </v-row>
                <v-row v-if="header.value === 'thumbnail'">
                  <v-sheet
                    class="ma-auto list-image-sheet"
                  >
                    <v-img
                      contain
                      class="ma-auto list-image"
                      :src="getSignedUrl(item.contents.thumbnailKey, true)"
                    />
                  </v-sheet>
                </v-row>
                <v-row
                  v-if="header.value === 'image'"
                  class="ma-auto"
                >
                  <v-sheet
                    class="ma-auto list-image-sheet"
                  >
                    <v-img
                      contain
                      class="ma-auto list-image"
                      :src="getSignedUrl(item.contents.imageKey, true)"
                    />
                  </v-sheet>
                </v-row>
                <v-row
                  v-if="header.value === 'video'"
                  class="ma-auto"
                >
                  <v-sheet
                    v-if="item.contents.videoKey !== null && item.contents.videoKey !== ''"
                    class="ma-auto list-image-sheet"
                  >
                    <video-player
                      id="vp"
                      class="ma-auto list-image"
                      :src="getSignedUrl(item.contents.videoKey, true)"
                    />
                  </v-sheet>
                </v-row>
              </td>
            </tr>
          </draggable>
        </template>
      </v-data-table>
    </v-card-text>
    <v-card-actions>
      <button-handler
        v-if="cardSaveBtnHandler()"
        cancel-destination="Album"
        :edit-mode="editMode"
        :target-index="targetIndex"
        @save="save"
        @update="save"
        @modify="toModify"
        @cancelModify="cancelModify"
      />
      <v-row
        v-if="backBtnHandler()"
        class="ma-auto"
        align-content="center"
        justify="center"
      >
        <v-btn
          class="mb-3"
          to="/Album"
        >
          {{ '뒤로' }}
        </v-btn>
      </v-row>
    </v-card-actions>
    <v-dialog
      v-model="cardFormActivator"
      overlay-color="#999999"
      max-width="800px"
    >
      <album-card-form
        :artist="artist"
        :album="album"
        :members="members"
        :handler="cardFormActivator"
        @close="closeCardDialog"
        @temporarySave="tempSave"
      />
    </v-dialog>
  </v-card>
</template>
<script >
  import store from '@/store'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import commonAddLanguageTableMixin from '@/mixins/commonAddLanguageTableMixin'
  import tagFormatMixin from '@/mixins/tagFormatMixin'
  import calcOffsetMixin from '@/mixins/calcOffsetMixin'
  import rulesMixin from '@/mixins/rulesMixin'
  import commonFormMixin from '@/mixins/commonFormMixin'
  import commonConfirmMixin from '@/mixins/commonConfirmMixin'
  import fileUploadMixin from '@/mixins/fileUploadMixin'
  import draggable from 'vuedraggable'
  import AlbumCardForm from './AlbumCardForm'
  import VideoPlayer from '@/components/common/VideoPlayer'
  import ButtonHandler from '@/components/common/ButtonHandler'
  import axios from '@/axios/index'
  import ApiList from '@/axios/api-list'
  import { v4 as uuidv4 } from 'uuid'
  import { s3PathConst, errorMsg, common, operationType, uploadType, albumState } from '@/assets/enums'
  import albumRoleMixin from '@/mixins/albumRoleMixin'
  import s3GetUrlMixin from '@/mixins/s3GetUrlMixin'

  export default {
    name: 'AlbumCardSection',
    components: {
      draggable,
      AlbumCardForm,
      VideoPlayer,
      ButtonHandler,
    },
    mixins: [
      commonErrorMixin,
      commonAddLanguageTableMixin,
      tagFormatMixin,
      calcOffsetMixin,
      rulesMixin,
      commonFormMixin,
      commonConfirmMixin,
      fileUploadMixin,
      albumRoleMixin,
      s3GetUrlMixin,
    ],
    props: {
      album: Object,
      artist: Object,
      members: Array,
    },
    data: () => ({
      editMode: false,
      targetItem: JSON.parse(JSON.stringify(store.state.common.targetItem)),
      cardFormActivator: false,
      cards: [],
      cardHeader: [
        {
          text: '',
          value: 'order',
        },
        {
          text: '순서',
          value: 'position',
        },
        {
          text: '썸네일',
          value: 'thumbnail',
        },
        {
          text: '컨텐츠 타입',
          value: 'type',
        },
        {
          text: '컨텐츠 이미지',
          value: 'image',
        },
        {
          text: '컨텐츠 비디오',
          value: 'video',
        },
        {
          text: '멤버',
          value: 'members',
        },
        {
          text: '',
          value: 'actions',
        },
      ],
    }),
    computed: {
      cardsNotDeleted () {
        return this.cards.filter(c => c.operationType !== operationType.DELETE)
      },
    },
    watch: {
      cards: {
        deep: true,
        handler () {
          if (this.album.state.code === albumState.WORKING) {
            if (this.cards.length <= 0) {
              this.editMode = true
            }
          }
        },
      },
    },
    created () {
      this.cards = JSON.parse(JSON.stringify(this.targetItem.cards))
    },
    methods: {
      backBtnHandler () {
        if (this.isEditor) {
          // 신규
          if (this.targetIndex < 0) return false
        }
        // 작업중이 아닐 때
        if (this.isState(albumState.WORKING)) return false

        return true
      },
      cardSaveBtnHandler () {
        if (this.cards.length <= 0) return false
        if (this.isEditor) {
          // 신규
          if (this.targetIndex < 0) return true
          // 작업중
          if (this.isState(albumState.WORKING)) return true
        }
        return false
      },
      async addCard () {
        await store.commit('resetTargetAlbumCard')
        this.cardFormActivator = true
      },
      async cardEdit (idx, item) {
        await store.commit('setTargetAlbumCard', item)
        await store.commit('setTargetAlbumCardIndex', idx)
        this.cardFormActivator = true
      },
      getUploadPath () {
        const pathArr = []
        pathArr.push(s3PathConst.ARTISTS)
        pathArr.push(this.album.artistId)
        pathArr.push(s3PathConst.ALBUMS)
        pathArr.push(this.album.id)
        pathArr.push(s3PathConst.CARDS)
        return pathArr.join(s3PathConst.DELIMITER)
      },
      async save () {
        await store.dispatch('setLoadingTrue')
        // when card temp-add: opType = INSERT (done)
        // when card temp-remove: opType = DELETE (done)
        for await (const card of this.cards) {
          // contents.type 이 스트링값이 아닌 경우 === 해당 값에 변함이 없는 경우. 코드값으로 대체하여 업데이트를 진행한다...
          if (typeof card.contents.type !== 'string') {
            const type = card.contents.type.code
            card.contents.type = type
          }
          // operationType 이 null 인 경우 === 해당 카드 자체가 편집되지 않은 경우.
          if (card.operationType === null) {
            card.operationType = operationType.NONE
          }
          // operationType 이 스트링값이 아닌 경우 === 해당 값에 변함이 없는 경우. 코드값으로 대체하여 업데이트를 진행한다...
          if (typeof card.operationType !== 'string') {
            const type = card.operationType.code
            card.operationType = type
          }
        }
        const path = this.getUploadPath()
        const filteredCards = this.cards
          .filter(c => c.operationType === operationType.INSERT || c.operationType === operationType.UPDATE)
          .filter(c => c.uploadedFiles !== undefined)
        if (!await this.imageUpload(path, filteredCards)) {
          // TODO: delete uploaded files ?
          this.setError(errorMsg.ERROR_WHILE_UPLOADING)
        }

        const req = []
        for await (const card of this.cards) {
          let thumbnail, image, video
          if (card.uploadedFiles !== undefined) {
            // modified
            const t = card.uploadedFiles.find(uf => uf.refname === uploadType.THUMBNAIL)
            const i = card.uploadedFiles.find(uf => uf.refname === uploadType.IMAGE)
            const v = card.uploadedFiles.find(uf => uf.refname === uploadType.VIDEO)
            thumbnail = t.modified ? t.key : card.contents.thumbnailKey
            image = i.modified ? i.key : card.contents.imageKey
            video = v.modified ? v.key : card.contents.videoKey
          } else {
            // not modified
            thumbnail = card.contents.thumbnailKey === null ? common.EMPTY : card.contents.thumbnailKey
            image = card.contents.imageKey === null ? common.EMPTY : card.contents.imageKey
            video = card.contents.videoKey === null ? common.EMPTY : card.contents.videoKey
          }
          const contents = {
            thumbnailKey: thumbnail,
            type: card.contents.type,
            imageKey: image,
            videoKey: video,
          }
          const data = {
            id: card.id,
            position: card.position,
            tags: card.tags,
            members: card.members,
            contents: contents,
            operationType: card.operationType,
          }
          req.push(data)
        }
        axios.post(ApiList.cardWithWorkingAlbumId(this.album.id), req).then(async res => {
          this.targetItem.cards = res.data.data.cards.sort((a, b) => a.position - b.position)
          await store.commit('setTargetItem', this.targetItem)
          this.cards = JSON.parse(JSON.stringify(store.state.common.targetItem.cards))
          store.dispatch('setLoadingFalse').then(() => {
            // do nothing
          })
        }).catch(err => {
          this.setError(err)
        })
        this.editMode = false
      },
      async toModify () {
        this.editMode = true
      },
      async cancelModify () {
        const storeCards = store.state.common.targetItem.cards
        this.cards = storeCards === null ? [] : JSON.parse(JSON.stringify(storeCards))
        this.editMode = this.cards.length <= 0
      },
      handleMemberName (memberArr) {
        const arr = []
        for (const memberId of memberArr) {
          const mem = this.members.find(m => m.id === memberId)
          if (mem !== undefined) {
            const target = mem.values.kr ? mem.values.kr : mem.values.en
            arr.push(target)
          }
        }
        return arr.join(', ')
      },
      tempSave (card) {
        if (card.operationType === operationType.INSERT) {
          // insert
          card.id = uuidv4()
          card.position = this.cards.filter(c => c.position > 0).length + 1
          card.albumId = this.album.id
          card.artistId = this.album.artistId
          for (const fileInfo of card.uploadedFiles) {
            fileInfo.cardId = card.id
          }
          this.cards.push(card)
        } else {
          // update
          const idx = this.cards.findIndex(c => c.id === card.id)
          this.cards.splice(idx, 1, card)
        }
        this.closeCardDialog()
      },
      closeCardDialog () {
        this.cardFormActivator = false
      },
      sortCards (items) {
        for (const [idx, item] of items.entries()) {
          item.position = idx + 1
        }
        this.cards = items.sort((a, b) => a.position - b.position)
      },
      async removeRow (item) {
        // 다른 카드들의 포지션을 재계산
        for (const c of this.cards.filter(c => c.position > item.position)) {
          c.position = c.position - 1
        }
        item.operationType = operationType.DELETE
        item.position = -1

        const index = this.cards.findIndex(c => c.id === item.id)
        this.$set(this.cards, index, item)
      },
    },
  }
</script>
<style>
.x-btn {
  z-index: 2;
  width: -webkit-fill-available;
  height: -webkit-fill-available;
  justify-content: flex-end;
}
.table-header {
  height: 70px;
}
.table-row-height {
  height: 150px;
}
</style>
