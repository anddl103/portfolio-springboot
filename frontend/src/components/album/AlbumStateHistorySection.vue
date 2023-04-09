<template>
  <v-card
    class="ma-auto"
  >
    <v-row class="ma-auto">
      <v-col cols="12">
        앨범 상태 변경 이력
      </v-col>
      <v-col>
        <v-data-table
          :headers="headers"
          :items="history"
          class="elevation-1"
        >
          <template v-slot:item.updatedAt="{ item }">
            {{ moment.utc(item.updatedAt).tz('Asia/Seoul').format('YYYY-MM-DD | HH : mm : ss') }}
          </template>
          <template v-slot:item.comment="{ item }">
            <v-row
              class="ma-auto"
              v-html="formatHtml(item.comment)"
            />
          </template>
        </v-data-table>
      </v-col>
    </v-row>
  </v-card>
</template>
<script>
  import store from '@/store/index'

  export default {
    name: 'AlbumStateHistorySection',
    data: () => ({
      expanded: [],
      singleExpand: false,
      headers: [
        {
          text: '번호',
          value: 'number',
          sortable: false,
          width: 100,
          align: 'center',
        },
        {
          text: '갱신일시',
          value: 'updatedAt',
          width: 200,
          align: 'center',
        },
        {
          text: '버전',
          value: 'version',
          sortable: false,
          width: 100,
          align: 'end',
        },
        {
          text: '상태',
          value: 'state.description',
          sortable: false,
          width: 200,
          align: 'center',
        },
        {
          text: '비고',
          value: 'comment',
          sortable: false,
        },
      ],
    }),
    computed: {
      history () {
        const arr = store.state.common.albumStateHistory
        return arr.sort((a, b) => b.number - a.number)
      },
    },
    methods: {
      formatHtml (text) {
        if (text === null) return
        const regex = new RegExp('\\n', 'g')
        return text.replaceAll(regex, '<br />')
      },
    },
  }
</script>
<style scoped>
</style>
