<template>
  <v-container
    class="pa-4"
    fluid
  >
    <common-confirm-dialog
      :dialog-handler.sync="confirmDialog"
      :dialog-content="confirmDialogContent"
      @confirm="confirmSubmit"
    />

    <v-row>
      <v-col
        offset="1"
        cols="6"
      >
        <v-card>
          <common-error-alert
            :error="error"
            :error-msg="errorMsg"
            @close="resetError"
          />

          <v-card-text>
            <v-form>
              <v-row class="ma-auto">
                <v-autocomplete
                  v-model="artistId"
                  :items="artists"
                  label="아티스트"
                  clearable
                  item-text="name"
                  item-value="id"
                />
              </v-row>
              <v-row class="ma-auto">
                <v-text-field
                  v-model="uid"
                  label="UID"
                  value="uid"
                />
              </v-row>
              <v-row class="ma-auto">
                <v-slider
                  v-model="percent"
                  min="0"
                  max="100"
                  step="10"
                  ticks
                  :tick-labels="tickLabels"
                  thumb-label
                  label="앨범 내 카드 수집율"
                >
                  <template v-slot:append>
                    <v-text-field
                      v-model="percent"
                      class="mt-0 pt-0"
                      type="number"
                      style="width: 60px"
                    />
                  </template>
                </v-slider>
              </v-row>
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-btn
              block
              color="secondary"
              @click="generate"
            >
              생성
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
<script >
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import store from '@/store'
  import commonConfirmMixin from '@/mixins/commonConfirmMixin'

  export default {
    name: 'Tool',
    mixins: [
      commonErrorMixin,
      commonConfirmMixin,
    ],
    data: () => ({
      artistId: '',
      uid: '',
      percent: 0,
      artists: [],
      tickLabels: [
        0, 10, 20, 30, 40, 50, 60, 70, 80, 90,
      ],
    }),
    mounted () {
      this.init()
    },
    methods: {
      init () {
        this.getArtists()
      },
      getArtists () {
        axios.get(ApiList.artists).then(res => {
          this.artists = res.data.data
        })
      },
      save () {
        // do nothing
      },
      generate () {
        store.dispatch('setLoadingTrue')
        const req = {
          artistId: this.artistId,
          uid: this.uid,
          percent: this.percent,
        }

        axios.post(ApiList.demo, req).then(() => {
          this.artistId = ''
          this.uid = ''
          this.percent = 0
          store.dispatch('setLoadingFalse')
          this.openConfirmDialog('저장이 완료되었습니다.')
        }).catch(err => {
          console.log(err)
          store.dispatch('setLoadingFalse')
          this.openConfirmDialog('에러가 발생했습니다. 콘솔을 확인해주세요.')
        })
      },
    },
  }
</script>
