<template>
  <v-dialog
    v-model="open"
    max-width="500px"
    @click:outside="close"
    @keydown.esc="close"
  >
    <template v-slot:activator="{ }" />
    <v-card>
      <common-error-alert
        :error="error"
        :error-msg="errorMsg"
        @close="resetError"
      />

      <v-card-title>
        <span class="text-h5">{{ formTitle }}</span>
      </v-card-title>

      <v-card-text>
        <v-container>
          <v-form
            ref="dialogForm"
            v-model="dialogValid"
            lazy-validation
          >
            <v-row>
              <v-text-field
                v-model="targetItem.email"
                outlined
                :disabled="targetIndex !== -1"
                :rules="emailRules"
                label="이메일"
              />
            </v-row>
            <v-row>
              <v-text-field
                v-if="targetIndex === -1 || targetItem.uid === currentAdmin.uid"
                v-model="targetItem.password"
                :append-icon="showPwd ? 'mdi-eye' : 'mdi-eye-off'"
                :type="showPwd ? 'text' : 'password'"
                outlined
                label="패스워드"
                counter
                :rules="targetIndex === -1 ? pwdRules : []"
                @click:append="showPwd = !showPwd"
              />
            </v-row>
            <v-row>
              <v-autocomplete
                v-model="targetItem.role"
                label="권한"
                outlined
                :disabled="roleFormControl"
                :items="roles"
                :rules="roleRules"
                item-text="description"
                item-value="code"
              />
            </v-row>
          </v-form>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          text
          @click="close"
        >
          취소
        </v-btn>
        <v-btn
          color="blue darken-1"
          text
          @click="targetIndex === -1 ? save() : update()"
        >
          저장
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script>
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import store from '@/store'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import { role } from '@/assets/enums'

  const constant = Object.freeze({
    eventName: {
      CLOSE: 'close',
      AFTER_SAVE: 'afterSave',
      AFTER_UPDATE: 'afterUpdate',
    },
  })

  export default {
    name: 'AdminForm',
    components: {
      CommonErrorAlert,
    },
    mixins: [
      commonErrorMixin,
    ],
    props: {
      open: Boolean,
      targetItem: Object,
      targetIndex: Number,
      formTitle: String,
      tableData: Array,
      currentAdmin: Object,
    },
    data: () => ({
      showPwd: false,
      dialogValid: true,
      originRoles: [],
      emailRules: [
        v => !!v || '필수 항목입니다.',
        v => /.+@.+\..+/.test(v) || '이메일 형식이 맞지 않습니다.',
      ],
      roleRules: [
        v => !!v || '필수 항목입니다.',
      ],
      dialog: false,
      dialogDelete: false,
      loading: false,
      defaultItem: {},
    }),
    computed: {
      roleFormControl () {
        if (this.currentAdmin.role === role.SUPER_ADMIN) {
          // 수퍼 어드민이면 disable = false
          return false
        }
        return true
      },
      roles () {
        const rolesForSuperAdmin = JSON.parse(JSON.stringify(this.originRoles))
        const rolesForAdmin = rolesForSuperAdmin.filter(r => r.code !== role.SUPER_ADMIN)
        return this.currentAdmin.role === role.SUPER_ADMIN ? rolesForSuperAdmin : rolesForAdmin
      },
      pwdRules () {
        return [
          v => {
            if (v === undefined || v === '') {
              return '필수 항목입니다.'
            }
            if (v.length < 8) {
              return '8자 이상이어야 합니다.'
            }
            return true
          },
        ]
      },
    },
    mounted () {
      this.init()
    },
    methods: {
      close () {
        this.$refs.dialogForm.resetValidation()
        this.$emit(constant.eventName.CLOSE)
      },
      validation () {
        return this.$refs.dialogForm.validate()
      },
      async save () {
        if (!this.validation()) return
        await store.dispatch('setLoadingTrue')
        const request = {
          email: this.targetItem.email,
          password: this.targetItem.password,
          role: this.targetItem.role,
        }
        axios.post(ApiList.admins, request).then(async (res) => {
          if (!this.validateResponse(res)) return
          this.$emit(constant.eventName.AFTER_SAVE)
          await this.close()
        }).catch(err => {
          this.setError(err)
        })
      },
      async update () {
        if (!this.validation()) return
        // 변함 없으면 패스
        if (this.targetItem.password === '') delete this.targetItem.password
        await store.dispatch('setLoadingTrue')
        const request = {
          uid: this.targetItem.uid,
          email: this.targetItem.email,
          role: this.targetItem.role,
        }
        if (this.targetItem.password !== undefined && this.targetItem.password !== '') {
          request.password = this.targetItem.password
        }
        axios.patch(ApiList.admins, request).then(res => {
          if (!this.validateResponse(res)) return
          this.$emit(constant.eventName.AFTER_UPDATE)
          this.close()
        }).catch(err => {
          this.setError(err)
        })
      },
      init () {
        this.originRoles = store.state.auth.roles
        this.defaultItem = JSON.parse(JSON.stringify(this.targetItem))
      },
    },
  }
</script>
