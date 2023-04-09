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
        <v-data-table
          :headers="tableHeader"
          :items="tableData"
          :loading="loading"
          class="elevation-1 vdt"
        >
          <template v-slot:top>
            <v-toolbar
              flat
            >
              <v-toolbar-title>어드민 목록</v-toolbar-title>
              <v-divider
                class="mx-4"
                inset
                vertical
              />
              <v-spacer />
              <v-divider
                class="mx-4"
                inset
                vertical
              />
              <v-btn
                color="primary"
                dark
                class="mb-2"
                @click="dialog = true"
              >
                어드민 추가
              </v-btn>
            </v-toolbar>
          </template>

          <!-- 생성일시 -->
          <template v-slot:item.createdAt="{ item }">
            <label class="v-label">{{ moment.utc(item.createdAt).tz('Asia/Seoul').format('YYYY-MM-DD | HH : mm : ss') }}</label>
          </template>

          <!-- 마지막 로그인 일시 -->
          <template v-slot:item.lastSignInTime="{ item }">
            <label class="v-label">{{ item.lastSignInTime === 0 ? '' : moment.utc(item.lastSignInTime).tz('Asia/Seoul').format('YYYY-MM-DD | HH : mm : ss') }}</label>
          </template>

          <template v-slot:item.actions="{ item }">
            <v-row>
              <v-btn
                v-if="item.uid === currentAdmin.uid || isSuperAdmin"
                tile
                plain
                text
                small
                outlined
                @click.stop="editItem(item)"
              >
                수정
              </v-btn>
              <!--빈칸 유지용 버튼-->
              <v-btn
                v-else
                tile
                plain
                text
                small
                disabled
              />
              <v-btn
                v-if="isSuperAdmin"
                right
                tile
                plain
                text
                small
                outlined
                @click.stop="deleteItem(item)"
              >
                삭제
              </v-btn>
              <!--빈칸 유지용 버튼-->
              <v-btn
                v-else
                tile
                plain
                text
                small
                disabled
              />
            </v-row>
          </template>
        </v-data-table>
      </v-col>
    </v-row>

    <dialog-form
      :open="dialog"
      :target-index="editedIndex"
      :target-item="editedItem"
      :form-title="editedIndex === -1 ? '생성' : '수정'"
      :current-admin="currentAdmin"
      @close="close"
      @afterSave="getTableData"
      @afterUpdate="getTableData"
    />

    <common-del-dialog
      :open="dialogDelete"
      :target="editedItem.email"
      :url="getDeleteUrl(editedItem.uid)"
      @close="closeDelete"
      @confirm="deleteItemConfirm"
    />
  </v-container>
</template>
<script>
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import store from '@/store'
  import CommonDelDialog from '@/components/common/CommonDelDialog'
  import DialogForm from './AdminForm'
  import dataTableMixin from '@/mixins/dataTableMixin'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import { role } from '@/assets/enums'

  export default {
    name: 'Admin',
    components: {
      CommonDelDialog,
      DialogForm,
      CommonErrorAlert,
    },
    mixins: [
      dataTableMixin,
      commonErrorMixin,
    ],
    data: () => ({
      showPwd: false,
      error: false,
      errorMsg: '',
      tableHeader: [
        {
          text: '이메일',
          value: 'email',
          sortable: false,
        },
        {
          text: 'UID',
          value: 'uid',
          sortable: false,
        },
        {
          text: '권한',
          value: 'role',
          sortable: false,
        },
        {
          text: '생성일시',
          value: 'createdAt',
          sortable: false,
        },
        {
          text: '마지막 로그인',
          value: 'lastSignInTime',
          sortable: false,
        },
        {
          text: '',
          value: 'actions',
          width: 200,
          sortable: false,
        },
      ],
      tableData: [],
      originRoles: [],
      currentAdmin: {},
      editedIndex: -1,
      editedItem: {
        uid: '',
        email: '',
        password: '',
        role: '',
      },
      originEditedItem: {
        uid: '',
        email: '',
        password: '',
        role: '',
      },
      defaultItem: {
        uid: '',
        email: '',
        password: '',
        role: '',
      },
      dialog: false,
      dialogDelete: false,
      loading: false,
    }),
    computed: {
      roles () {
        const rolesForSuperAdmin = JSON.parse(JSON.stringify(this.originRoles))
        const rolesForAdmin = rolesForSuperAdmin.filter(r => r.code !== role.SUPER_ADMIN)
        return this.currentAdmin.role === role.SUPER_ADMIN ? rolesForSuperAdmin : rolesForAdmin
      },
      isSuperAdmin () {
        return this.currentAdmin.role === role.SUPER_ADMIN
      },
    },
    mounted () {
      this.initialize()
    },
    methods: {
      getDeleteUrl (id) {
        return ApiList.adminWithId(id)
      },
      async getTableData () {
        this.loadingStart()
        axios.get(ApiList.admins).then(res => {
          this.loadingDone()
          if (!this.validateResponse(res)) return
          this.tableData = res.data.data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        }).catch((err) => {
          this.loadingDone()
          this.setError(err)
        })
      },
      async getCurrentAdmin () {
        const uid = store.state.auth.uid
        axios.get(ApiList.adminWithId(uid)).then(res => {
          if (!this.validateResponse(res)) return
          this.currentAdmin = res.data.data
        }).catch((err) => {
          this.setError(err)
        })
      },
      initialize () {
        this.getTableData()
        this.getCurrentAdmin()
      },
    },
  }
</script>
