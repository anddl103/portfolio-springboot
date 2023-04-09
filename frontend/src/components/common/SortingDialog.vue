<template>
  <v-dialog
    ref="dialog"
    v-model="handler"
    max-width="1000px"
    persistent
    @keydown.esc="close"
  >
    <common-error-alert
      :error="error"
      :error-msg="errorMsg"
      @close="resetError"
    />

    <v-data-table
      :headers="tableHeader"
      :items="tableData"
      hide-default-footer
    >
      <template v-slot:body="{ items }">
        <draggable
          :list="items"
          direction="h"
          tag="tbody"
          class="width-webkit-fill"
          @update="setOrder(items)"
        >
          <row-handler
            v-for="(item, index) in items"
            :key="index"
            :item="item"
            :headers="tableHeader"
          >
            <template v-slot:item.rowNum>
              {{ index + 1 }}
            </template>
            <template v-slot:item.tags>
              {{ formatTags(item.tags) }}
            </template>
            <template v-slot:item.thumbnailKey>
              <v-img
                class="my-2"
                :src="item.thumbnailKey"
                :aspect-ratio="4/3"
                max-width="70"
                max-height="70"
              />
            </template>
            <template v-slot:item.album>
              <v-row
                class="mx-auto my-3"
                align="center"
              >
                <v-avatar left>
                  <v-img
                    class="my-2"
                    :src="getSignedUrl(item.album.thumbnailKey, true)"
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
                {{ item.album.title }}
              </v-row>
            </template>
            <!-- 아티스트 -->
            <template v-slot:item.artist>
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
                      :src="getArtistUrl(item)"
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
            <!-- 배너: 앨범 -->
            <template v-slot:item.bannerAlbum>
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
            <template v-slot:item.dragger>
              <v-icon>
                mdi-swap-vertical
              </v-icon>
            </template>
          </row-handler>
        </draggable>
      </template>
    </v-data-table>
    <v-row class="ma-auto">
      <v-col>
        <v-btn
          block
          @click="cancel"
        >
          취소
        </v-btn>
      </v-col>
      <v-col>
        <v-btn
          color="primary"
          block
          @click="save"
        >
          저장
        </v-btn>
      </v-col>
    </v-row>
  </v-dialog>
</template>
<script >
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import draggable from 'vuedraggable'
  import RowHandler from '@/components/common/RowHandler'
  import tagFormatMixin from '@/mixins/tagFormatMixin'
  import s3GetUrlMixin from '@/mixins/s3GetUrlMixin'

  export default {
    name: 'SortingDialog',
    components: {
      CommonErrorAlert,
      RowHandler,
      draggable,
    },
    mixins: [
      tagFormatMixin,
      commonErrorMixin,
      s3GetUrlMixin,
    ],
    props: {
      handler: Boolean,
      tableHeader: Array,
      tableData: Array,
    },
    data: () => ({
      orderedList: [],
    }),
    methods: {
      cancel () {
        this.$emit('stateSync', false)
      },
      save () {
        this.$emit('updateOrder', this.orderedList)
        this.cancel()
      },
      close () {
        this.cancel()
      },
      setOrder (arr) {
        for (let i = 0; i < arr.length; i++) {
          arr[i].sortOrder = i + 1
        }
        this.orderedList = arr
      },
      getArtistName (item) {
        return item.artist ? item.artist.name : item.name
      },
      getArtistUrl (item) {
        let key = ''
        if (item.artist) {
          key = item.artist.logoKey
        } else {
          key = item.logoKey
        }
        return this.getSignedUrl(key)
      },
    },
  }
</script>
