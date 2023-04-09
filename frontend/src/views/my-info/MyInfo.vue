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

    <v-row>
      <v-col
        offset="1"
        cols="10"
      >
        <v-card>
          <v-card-title>
            ::: TOKEN :::
          </v-card-title>
          <v-card-text>
            <v-data-table
              class="width-webkit-fill"
              :items="tableData"
              single-expand
              hide-default-footer
              hide-default-header
            >
              <template v-slot:item="{ item }">
                <tr>
                  <td class="header">
                    {{ item.td1.toUpperCase() }}
                  </td>
                  <td
                    v-if="item.td1 === 'token'"
                    class="contents"
                  >
                    <v-btn
                      :color="copied? '' : 'secondary'"
                      @click="copy(item.td2)"
                    >
                      {{ copied ? '복사됨' : 'COPY' }}
                    </v-btn>
                    <v-btn
                      @click="showToken"
                    >
                      {{ expanded ? '토큰 닫기' : '토큰 보기' }}
                    </v-btn>
                  </td>
                  <td
                    v-if="item.td1 === ''"
                    class="expanded"
                  >
                    {{ item.td2 }}
                  </td>
                  <td v-if="item.td1 !== 'token' && item.td1 !== ''">
                    <pre>{{ item.td2 === null ? 'null' : item.td2 }}</pre>
                  </td>
                </tr>
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <input
      id="token"
      type="hidden"
    >
  </v-container>
</template>
<script >
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import { firebaseAuth } from '@/firebase'

  export default {
    name: 'MyInfo',
    components: {
      CommonErrorAlert,
    },
    mixins: [
      commonErrorMixin,
    ],
    data: () => ({
      token: '',
      current: {},
      tableData: [],
      copied: false,
      expanded: false,
    }),
    mounted () {
      this.init()
    },
    methods: {
      init () {
        this.getCurrentUserInfo()
      },
      showToken () {
        if (this.expanded) {
          this.expanded = false
          this.tableData.splice(1, 1)
        } else {
          this.expanded = true
          this.tableData.splice(1, 0, { td1: '', td2: this.current.token })
        }
      },
      copy (val) {
        const testingCodeToCopy = document.querySelector('#token')
        testingCodeToCopy.setAttribute('type', 'text')
        testingCodeToCopy.setAttribute('value', val)
        testingCodeToCopy.select()
        document.execCommand('copy')

        /* unselect the range */
        testingCodeToCopy.setAttribute('type', 'hidden')
        window.getSelection().removeAllRanges()
        this.copied = true
        setTimeout(this.setCopyStateFalse, 3000)
      },
      setCopyStateFalse () {
        this.copied = false
      },
      getCurrentUserInfo () {
        this.tableData = []
        const user = firebaseAuth.currentUser
        user.getIdTokenResult().then(res => {
          this.current = res
          const entries = Object.entries(this.current)
          for (const entry of entries) {
            const dataObj = {}
            dataObj.td1 = entry[0]
            dataObj.td2 = entry[1]
            this.tableData.push(dataObj)
          }
        }).catch(err => {
          this.setError(err)
        })
      },
    },
  }
</script>
<style>
#info {
  padding:10px;
  overflow: auto;
  white-space: pre-wrap;       /* Since CSS 2.1 */
  white-space: -moz-pre-wrap;  /* Mozilla, since 1999 */
  word-wrap: break-word;       /* Internet Explorer 5.5+ */
}
.header {
  width: 200px !important;
}
.contents {
  width: 600px !important;
  min-width: 400px !important;
  max-width: 800px !important;
}
.expanded {
  width: 500px !important;
  min-width: 400px !important;
  max-width: 600px !important;
}
</style>
