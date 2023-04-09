<template>
  <v-card>
    <v-row
      class="ma-auto align-center"
    >
      <v-col>
        카드 정보
      </v-col>
    </v-row>
    <v-row
      v-if="cards.length > 0"
      class="ma-auto width-webkit-fill"
      justify="center"
      align="center"
    >
      <v-col cols="12">
        <v-data-table
          :items="cards"
          :headers="cardHeader"
          hide-default-footer
          :item-class="rowClass"
        >
          <template v-slot:item.position="{ item }">
            <v-row
              class="my-auto mx-3"
              justify="center"
            >
              {{ item.position }}
            </v-row>
          </template>
          <template v-slot:item.thumbnail="{ item }">
            <v-sheet
              class="ma-auto list-image-sheet"
            >
              <v-img
                contain
                class="ma-auto list-image"
                :src="getSignedUrl(item.contents.thumbnailKey, false)"
              />
            </v-sheet>
          </template>
          <template v-slot:item.type="{ item }">
            <v-row
              class="my-auto mx-3"
              justify="center"
            >
              {{ item.contents.type.description }}
            </v-row>
          </template>
          <template v-slot:item.image="{ item }">
            <v-sheet
              class="ma-auto list-image-sheet"
            >
              <v-img
                contain
                class="ma-auto list-image"
                :src="getSignedUrl(item.contents.imageKey, false)"
              />
            </v-sheet>
          </template>
          <template v-slot:item.video="{ item }">
            <v-sheet
              v-if="item.contents.videoKey !== null && item.contents.videoKey !== ''"
              class="ma-auto list-image-sheet"
            >
              <video-player
                id="vp"
                class="ma-auto list-image"
                :src="getSignedUrl(item.contents.videoKey, false)"
              />
            </v-sheet>
          </template>
          <template v-slot:item.members="{ item }">
            {{ handleMemberName(item.members) }}
          </template>
        </v-data-table>
      </v-col>
    </v-row>
  </v-card>
</template>
<script >
  import store from '@/store'
  import VideoPlayer from '@/components/common/VideoPlayer'
  import s3GetUrlMixin from '@/mixins/s3GetUrlMixin'

  export default {
    name: 'AlbumDeployedCardSection',
    components: {
      VideoPlayer,
    },
    mixins: [
      s3GetUrlMixin,
    ],
    props: {
      album: Object,
      artist: Object,
      members: Array,
    },
    data: () => ({
      targetItem: JSON.parse(JSON.stringify(store.state.common.targetItem)),
      cards: store.state.common.targetItem.cards ? JSON.parse(JSON.stringify(store.state.common.targetItem.cards)) : [],
      cardHeader: [
        {
          text: '순서',
          value: 'position',
          align: 'center',
          sortable: false,
        },
        {
          text: '썸네일',
          value: 'thumbnail',
          align: 'center',
          sortable: false,
        },
        {
          text: '컨텐츠 타입',
          value: 'type',
          align: 'center',
          sortable: false,
        },
        {
          text: '컨텐츠 이미지',
          value: 'image',
          align: 'center',
          sortable: false,
        },
        {
          text: '컨텐츠 비디오',
          value: 'video',
          align: 'center',
          sortable: false,
        },
        {
          text: '멤버',
          value: 'members',
          align: 'center',
          sortable: false,
        },
      ],
    }),
    methods: {
      rowClass (item) {
        return 'table-row-height'
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
    },
  }
</script>
