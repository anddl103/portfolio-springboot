<template>
  <v-dialog
    v-model="dialog"
    width="500"
  >
    <template v-slot:activator="{ on, attrs }">
      <v-btn
        class="mx-3"
        depressed
        outlined
        v-bind="attrs"
        v-on="on"
      >
        변경
      </v-btn>
    </template>
    <v-card>
      <v-card-title class="text-h5 accent white--text">
        비밀번호 변경
      </v-card-title>
      <v-card-text>
        <v-form v-model="valid">
          <v-container fluid>
            <v-text-field
              v-model="currentPassword"
              :append-icon="currentPasswordShow ? 'mdi-eye' : 'mdi-eye-off'"
              :rules="[rules.required, rules.min]"
              :type="currentPasswordShow ? 'text' : 'password'"

              label="Current Password"
              hint="At least 8 characters"
              counter
              @click:append="currentPasswordShow = !currentPasswordShow"
            />
            <v-text-field
              v-model="newPassword"
              :append-icon="newPasswordShow ? 'mdi-eye' : 'mdi-eye-off'"
              :rules="[rules.required, rules.min]"
              :type="newPasswordShow ? 'text' : 'password'"

              label="New Password"
              hint="At least 8 characters"
              counter
              @click:append="newPasswordShow = !newPasswordShow"
            />
            <v-text-field
              v-model="confirmPassword"
              :append-icon="confirmPasswordShow ? 'mdi-eye' : 'mdi-eye-off'"
              :rules="[rules.required, rules.min]"
              :type="confirmPasswordShow ? 'text' : 'password'"

              label="Confirm Password "
              hint="At least 8 characters"
              counter
              @click:append="confirmPasswordShow = !confirmPasswordShow"
            />
          </v-container>
        </v-form>
      </v-card-text>
      <v-divider />
      <v-card-actions>
        <v-spacer />
        <v-btn
          color="primary"
          text
          @click="dialog = false"
        >
          취소
        </v-btn>
        <v-btn
          color="primary"
          text
          @click="pwChange(newPassword)"
        >
          저장
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script>
  export default {
    name: 'AdminPwChange',
    data () {
      return {
        dialog: false,
        valid: false,
        currentPasswordShow: false,
        newPasswordShow: false,
        confirmPasswordShow: false,
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
        rules: {
          required: (value) => !!value || 'Required.',
          min: (v) => v.length >= 8 || 'Min 8 characters',
        },
      }
    },
    methods: {
      async pwChange (password) {
        console.log('------------- pwChange method -------------')
        this.dialog = false
        await this.$store.dispatch('patchPassword', { password })
      },
      openPwDialog () {
        this.dialog = true
      },
    },
  }
</script>
