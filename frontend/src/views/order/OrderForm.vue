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
        주문 정보
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
              @change="filterAlbum"
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
              item-text="id"
              return-object
              :items="filteredAlbums"
              :rules="[required]"
              :readonly="!editMode"
              :append-icon="editMode ? undefined : ''"
              :class="!editMode ? 'disable-events' : ''"
              @change="filterNfcCardList"
            >
              <template v-slot:selection="{ item }">
                <v-avatar>
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
                {{ formatTags(item.tags) }}
              </template>
              <template v-slot:item="{ item }">
                <v-avatar>
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
                {{ formatTags(item.tags) }}
              </template>
            </v-autocomplete>
          </v-row>
          <v-row>
            <v-autocomplete
              v-model="selectedNfcCard"
              label="NFC 카드"
              item-text="id"
              return-object
              :items="filteredNfcCards"
              :rules="[required]"
              :readonly="!editMode"
              :append-icon="editMode ? undefined : ''"
              :class="!editMode ? 'disable-events' : ''"
            >
              <template v-slot:selection="{item}">
                <v-avatar
                  v-for="card in item.cards"
                  :key="card.id"
                  left
                >
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
                {{ formatTags(item.tags) === undefined ? item.comment : formatTags(item.tags) }}
              </template>
              <template v-slot:item="{item}">
                <v-avatar
                  v-for="card in item.cards"
                  :key="card.id"
                >
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
                {{ formatTags(item.tags) === undefined ? item.comment : formatTags(item.tags) }}
              </template>
            </v-autocomplete>
          </v-row>
          <v-row>
            <v-text-field
              v-model="targetItem.quantity"
              label="수량"
              :rules="quantityRules"
              :readonly="!editMode"
              :class="!editMode ? 'disable-events' : ''"
            />
          </v-row>
          <v-row>
            <v-text-field
              v-model="targetItem.comment"
              label="설명"
              :rules="[required]"
              :readonly="!editMode"
              :class="!editMode ? 'disable-events' : ''"
            />
          </v-row>
          <!--          <v-row>-->
          <!--            <v-autocomplete-->
          <!--              v-model="targetItem.region"-->
          <!--              label="서비스 지역"-->
          <!--              item-text="description"-->
          <!--              item-value="code"-->
          <!--              :items="regions"-->
          <!--              :rules="[required]"-->
          <!--              :disabled="isDetail"-->
          <!--            />-->
          <!--          </v-row>-->
        </v-form>
      </v-col>
    </v-row>
    <v-divider
      class="mt-3 mb-10"
    />
    <button-handler
      cancel-destination="Order"
      :edit-mode="editMode"
      :target-index="targetIndex"
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
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import store from '@/store'
  import tagFormatMixin from '@/mixins/tagFormatMixin'
  import rulesMixin from '@/mixins/rulesMixin'
  import commonFormMixin from '@/mixins/commonFormMixin'
  import ButtonHandler from '@/components/common/ButtonHandler'
  import { routeName } from '@/router'
  import s3GetUrlMixin from '@/mixins/s3GetUrlMixin'

  export default {
    name: 'OrderForm',
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
      regions: [],
      nfcCards: [],
      albums: [],
      artists: [],
      selectedArtist: null,
      filteredAlbums: [],
      selectedAlbum: null,
      filteredNfcCards: [],
      selectedNfcCard: null,
      targetItem: JSON.parse(JSON.stringify(store.state.common.targetItem)),
      formValid: true,
      editMode: false,
      isDetail: true,
    }),
    created () {
      this.init()
    },
    methods: {
      async init () {
        await this.getArtists()
        await this.getNfcCards()
        await this.setDefault()
        this.getRegions()
      },
      async setDefault () {
        if (this.targetIndex === -1) {
          this.isDetail = false
          this.editMode = true
          // init targetItem
          this.targetItem = {
            album: {},
          }
        } else {
          await this.filterNfcCardList(this.defaultItem.album)
          this.selectedArtist = this.artists.find(ar => ar.id === this.defaultItem.artist.id)
          await this.filterAlbum(this.selectedArtist)
          this.selectedAlbum = this.filteredAlbums.find(al => al.id === this.defaultItem.album.id)
          this.selectedNfcCard = this.filteredNfcCards.find(c => c.id === this.defaultItem.nfcCardId)
        }
      },
      async filterNfcCardList (album) {
        if (album === null) return

        const params = {
          albumId: album.id,
        }
        this.filteredNfcCards = await axios.get(ApiList.nfcs, { params }).then(res => {
          return res.data.data
        }).catch(err => this.setError(err))
      },
      async filterAlbum (item) {
        if (item === null || item === undefined) {
          this.filteredAlbums = []
          return
        }

        const params = {
          artistId: item.id,
        }
        this.filteredAlbums = await axios.get(ApiList.albums, { params }).then(res => {
          return res.data.data
        }).catch(err => this.setError(err))
      },
      async toModify () {
        this.editMode = true
      },
      cancelModify () {
        this.targetItem = JSON.parse(JSON.stringify(this.defaultItem))
        this.isDetail = true
        this.editMode = false
      },
      toList () {
        this.$router.push({ name: routeName.ORDER })
      },
      validation () {
        const con1 = this.checkForm(this.$refs.form)
        if (!this.isDetail) {
          return con1
        } else {
          const con2 = this.checkChanged(this.targetItem, this.defaultItem)
          if (!con2) {
            this.cancelModify()
          }
          return con1 && con2
        }
      },
      save () {
        if (!this.validation()) return
        store.dispatch('setLoadingTrue')
        const req = {
          nfcCardId: this.selectedNfcCard.id,
          quantity: this.targetItem.quantity,
          comment: this.targetItem.comment,
          region: this.regions[0].code,
        }
        axios.post(ApiList.orders, req).then(res => {
          if (!this.validateResponse(res)) return
          this.toList()
        }).catch(err => {
          this.setError(err)
        })
      },
      update () {
        if (!this.validation()) return
        store.dispatch('setLoadingTrue')
        const req = {
          quantity: this.targetItem.quantity,
          comment: this.targetItem.comment,
        }
        const id = this.targetItem.id
        axios.patch(ApiList.orderWithId(id), req).then(res => {
          if (!this.validateResponse(res)) return
          store.commit('setTargetItem', res.data.data)
          this.defaultItem = res.data.data
          this.editMode = false
        }).catch(err => {
          this.setError(err)
        })
      },
      async getNfcCards () {
        await axios.get(ApiList.nfcs).then(res => {
          if (!this.validateResponse(res)) return
          this.nfcCards = res.data.data
        }).catch(err => {
          this.setError(err)
        })
      },
      getRegions () {
        axios.get(ApiList.regions).then(res => {
          if (!this.validateResponse(res)) return
          this.regions = res.data.data
        }).catch(err => {
          this.setError(err)
        })
      },
      async getArtists () {
        await axios.get(ApiList.artists).then(res => {
          if (!this.validateResponse(res)) return
          this.artists = res.data.data
        }).catch(err => {
          this.setError(err)
        })
      },
    },
  }
</script>
<style>
</style>
