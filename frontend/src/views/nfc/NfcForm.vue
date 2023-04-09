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
        NFC 카드 정보
      </v-col>
      <v-col cols="8">
        <v-form
          ref="form"
          v-model="formValid"
          lazy-validation
        >
          <v-row>
            <v-autocomplete
              v-model="selectedArtist"
              label="아티스트"
              :clearable="editMode"
              item-text="id"
              return-object
              :items="artists"
              :rules="[required]"
              :readonly="!editMode"
              :append-icon="editMode ? undefined : ''"
              :class="!editMode ? 'disable-events' : ''"
            >
              <template v-slot:selection="{item}">
                <v-avatar left>
                  <v-img
                    :src="getSignedUrl(item.thumbnailKey)"
                    max-width="70"
                    max-height="70"
                  />
                </v-avatar>
                <v-divider
                  class="mx-4"
                  inset
                  vertical
                />
                {{ item.name }}
              </template>
              <template v-slot:item="{item}">
                <v-avatar left>
                  <v-img
                    :src="getSignedUrl(item.thumbnailKey)"
                    max-width="70"
                    max-height="70"
                  />
                </v-avatar>
                <v-divider
                  class="mx-4"
                  inset
                  vertical
                />
                {{ item.name }}
              </template>
            </v-autocomplete>
          </v-row>
          <v-row>
            <v-autocomplete
              v-model="selectedAlbum"
              label="앨범"
              :clearable="editMode"
              item-text="id"
              return-object
              :items="filteredAlbums"
              :rules="[required]"
              :readonly="!editMode"
              :append-icon="editMode ? undefined : ''"
              :class="!editMode ? 'disable-events' : ''"
            >
              <template v-slot:selection="{item}">
                <v-avatar left>
                  <v-img
                    :src="getSignedUrl(item.thumbnailKey)"
                    max-width="70"
                    max-height="70"
                  />
                </v-avatar>
                <v-divider
                  class="mx-4"
                  inset
                  vertical
                />
                {{ item.titleKr }}
              </template>
              <template v-slot:item="{item}">
                <v-avatar left>
                  <v-img
                    :src="getSignedUrl(item.thumbnailKey)"
                    max-width="70"
                    max-height="70"
                  />
                </v-avatar>
                <v-divider
                  class="mx-4"
                  inset
                  vertical
                />
                {{ item.titleKr }}
              </template>
            </v-autocomplete>
          </v-row>
          <v-row>
            <v-autocomplete
              v-model="selectedCards"
              label="하위 카드"
              item-text="tags"
              item-value="id"
              :clearable="editMode"
              multiple
              :items="albumCards"
              :rules="[required]"
              :readonly="!editMode"
              :append-icon="editMode ? undefined : ''"
              :class="!editMode ? 'disable-events' : ''"
            >
              <template v-slot:selection="{item}">
                <v-chip
                  close
                  :close-icon="editMode ? undefined : ''"
                  @click:close="removeCard(item)"
                >
                  <v-avatar left>
                    <v-img
                      :src="getSignedUrl(item.contents.thumbnailKey)"
                      max-width="70"
                      max-height="70"
                    />
                  </v-avatar>
                  {{ formatTags(item.tags) }}
                </v-chip>
              </template>
              <template v-slot:item="{ item }">
                <v-avatar left>
                  <v-img
                    :src="getSignedUrl(item.contents.thumbnailKey)"
                    max-width="70"
                    max-height="70"
                  />
                </v-avatar>
                <v-divider
                  class="mx-4"
                  inset
                  vertical
                />
                {{ formatTags(item.tags) }}
              </template>
            </v-autocomplete>
          </v-row>
          <v-row>
            <v-combobox
              v-model="tags"
              class="tag-input"
              label="검색용 태그"
              chips
              multiple
              deletable-chips
              :rules="[required]"
              :readonly="!editMode"
              :append-icon="editMode ? undefined : ''"
              :class="!editMode ? 'disable-events' : ''"
            />
          </v-row>
          <v-row>
            <v-textarea
              v-model="comment"
              outlined
              label="설명"
              :rules="[required]"
              :readonly="!editMode"
              :class="!editMode ? 'disable-events' : ''"
            />
          </v-row>
        </v-form>
      </v-col>
    </v-row>
    <v-divider
      class="mt-3 mb-10"
    />
    <button-handler
      cancel-destination="Nfc"
      :edit-mode="editMode"
      :target-index="targetIndex"
      :modify-text="handleModifyBtnStr"
      :modify-disable="handleModifyBtn"
      @save="save"
      @update="update"
      @modify="toModify"
      @cancelModify="cancelModify"
    />
  </v-container>
</template>
<script >
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import store from '@/store'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import tagFormatMixin from '@/mixins/tagFormatMixin'
  import rulesMixin from '@/mixins/rulesMixin'
  import commonFormMixin from '@/mixins/commonFormMixin'
  import ButtonHandler from '@/components/common/ButtonHandler'
  import { nfcCardStatus } from '@/assets/enums'
  import { routeName } from '@/router'
  import s3GetUrlMixin from '@/mixins/s3GetUrlMixin'

  const constant = Object.freeze({
    eventName: {
      AFTER_SAVE: 'afterSave',
    },
    targetKey: {
      NAME: 'name',
      TITLE: 'titleKr',
    },
  })

  export default {
    name: 'NfcForm',
    components: {
      CommonErrorAlert,
      ButtonHandler,
    },
    mixins: [
      commonErrorMixin,
      tagFormatMixin,
      rulesMixin,
      commonFormMixin,
      s3GetUrlMixin,
    ],
    data: () => ({
      formValid: true,
      albumCards: [],
      selectedCards: [],
      targetItem: JSON.parse(JSON.stringify(store.state.common.targetItem)),
      isDetail: true,
      editMode: false,
      artists: store.state.common.artists,
      filteredAlbums: [],
      selectedArtist: null,
      selectedAlbum: null,
      tags: [],
      watchable: false,
      comment: '',
    }),
    computed: {
      handleModifyBtn () {
        if (this.targetItem.status) {
          if (this.targetItem.status.code === nfcCardStatus.AFTER_ORDER) return true
        }
        return false
      },
      handleModifyBtnStr () {
        let str = '수정'
        if (this.targetItem.status) {
          if (this.targetItem.status.code === nfcCardStatus.AFTER_ORDER) str = '주문됨 (수정 불가)'
        }
        return str
      },

    },
    watch: {
      selectedArtist: {
        async handler (newVal, oldVal) {
          if (this.watchable) {
            if (newVal !== null) {
              await this.setFilteredAlbums(this.selectedArtist.id)
            } else {
              this.filteredAlbums = []
            }
            this.selectedAlbum = null
            if (this.selectedCards.length > 0) {
              this.selectedCards = []
            }
            this.autoSetTag(newVal, oldVal, constant.targetKey.NAME)
          }
        },
      },
      selectedAlbum: {
        handler (newVal, oldVal) {
          if (this.watchable) {
            if (newVal !== null) {
              this.getCardsByAlbum(this.selectedAlbum.id)
            } else {
              this.albumCards = []
            }
            if (this.selectedCards.length > 0) {
              this.selectedCards = []
            }
            this.autoSetTag(newVal, oldVal, constant.targetKey.TITLE)
          }
        },
      },
      defaultItem: {
        handler () {
          if (this.targetIndex > -1) {
            const arr = []
            for (const card of this.defaultItem.cards) {
              arr.push(card.id)
            }
            this.selectedCards = arr
          } else {
            this.selectedCards = []
          }
        },
        deep: true,
      },
    },
    mounted () {
      this.init()
    },
    methods: {
      async getCardsByAlbum (albumId) {
        axios.get(ApiList.cardWithAlbumId(albumId)).then(res => {
          if (!this.validateResponse(res)) return
          this.albumCards = res.data.data
        }).catch(err => this.setError(err))
      },
      async setFilteredAlbums (artistId) {
        const params = {
          artistId: artistId,
        }
        await axios.get(ApiList.albums, { params }).then(res => {
          if (!this.validateResponse(res)) return
          this.filteredAlbums = res.data.data
        }).catch(err => this.setError(err))
      },
      autoSetTag (newVal, oldVal, key) {
        if (newVal !== null) {
          if (oldVal !== null) {
            const idx = this.tags.findIndex(t => t === oldVal[key])
            if (idx >= 0) {
              this.tags.splice(idx, 1, newVal[key])
            } else {
              this.tags.push(newVal[key])
            }
          } else {
            const idx = this.tags.findIndex(t => t === newVal[key])
            if (idx < 0) {
              this.tags.push(newVal[key])
            }
          }
        } else {
          const idx = this.tags.findIndex(t => t === oldVal[key])
          this.tags.splice(idx, 1)
        }
      },
      async setSelectedsFromTargetItem () {
        this.selectedArtist = this.targetItem.artist
        await this.setFilteredAlbums(this.selectedArtist.id)
        this.selectedAlbum = this.filteredAlbums.find(al => al.id === this.targetItem.album.id)
        this.tags = this.targetItem.tags
        this.selectedCards = JSON.parse(JSON.stringify(this.targetItem.cards.map(a => a.id)))
        this.comment = this.targetItem.comment
      },
      async init () {
        this.editMode = this.targetIndex === -1
        this.isDetail = this.targetIndex !== -1
        if (this.targetIndex !== -1) {
          await this.setSelectedsFromTargetItem()
          await this.getCardsByAlbum(this.selectedAlbum.id)
        }
        this.watchable = true
        this.$refs.form.resetValidation()
      },
      async setInitSelectedCards () {
        this.selectedCards = JSON.parse(JSON.stringify(this.targetItem.cards.map(a => a.id)))
      },
      async toModify () {
        this.editMode = true
      },
      async cancelModify () {
        this.watchable = false
        this.targetItem = JSON.parse(JSON.stringify(this.defaultItem))
        await this.setSelectedsFromTargetItem()
        await this.getCardsByAlbum(this.selectedAlbum.id)
        this.isDetail = true
        this.editMode = false
        this.watchable = true
      },
      toList () {
        this.$router.push({ name: routeName.NFC })
      },
      validation () {
        this.resetError()
        const con1 = this.checkForm()
        if (this.targetIndex === -1) {
          return con1
        } else {
          const con2 = this.checkDataSame()
          if (con2) {
            // 현재와 같은 데이터
            this.cancelModify()
          }
          return con1 && !con2
        }
      },
      checkForm () {
        return this.$refs.form.validate()
      },
      checkDataSame () {
        // 갱신이 필요한지 체크: 같으면 true
        const con1 = JSON.stringify(this.targetItem) === JSON.stringify(this.defaultItem)
        const con2 = JSON.stringify(this.albumCards) === JSON.stringify(this.selectedCards)
        return con1 && con2
      },
      async save () {
        if (!this.validation()) return
        await store.dispatch('setLoadingTrue')
        const request = {
          albumId: this.selectedAlbum.id,
          tags: this.tags,
          comment: this.comment,
          cardIds: this.selectedCards,
        }
        axios.post(ApiList.nfcs, request).then(res => {
          if (!this.validateResponse(res)) return
          this.$emit(constant.eventName.AFTER_SAVE, res.data.data)
          this.toList()
        }).catch(err => {
          this.setError(err)
        })
      },
      async update () {
        if (!this.validation()) return
        await store.dispatch('setLoadingTrue')

        // 최종 선택된 앨범아이디에 포함되어있는 카드만 selectedCards 에서 추려서 업데이트.
        const selectedCards = []
        for (const card of this.albumCards) {
          const matched = this.selectedCards.find(a => a === card.id)
          selectedCards.push(matched)
        }

        const request = {
          albumId: this.selectedAlbum.id,
          tags: this.tags,
          comment: this.comment,
          cardIds: selectedCards,
        }
        const id = this.targetItem.id
        axios.patch(ApiList.nfcWithId(id), request).then(res => {
          if (!this.validateResponse(res)) return
          this.defaultItem = res.data.data
          this.selectedCards = this.defaultItem.cardIds
          this.editMode = false
        }).catch(err => {
          this.setError(err)
        })
      },
      removeCard (item) {
        const idx = this.selectedCards.findIndex(c => c === item.id)
        if (idx >= 0) this.selectedCards.splice(idx, 1)
      },
    },
  }
</script>
<style>
</style>
