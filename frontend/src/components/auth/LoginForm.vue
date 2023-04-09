<template>
  <v-row class="d-flex justify-center">
    <v-card
      class="justify-center"
      elevation="2"
      width="400"
    >
      <v-card-title class="justify-center">
        <h1>Smart Photo Card B.O</h1>
      </v-card-title>
      <v-divider />
      <v-card-subtitle>
        <v-alert
          v-if="alertFlag"
          v-model="alertFlag"
          type="error"
          dismissible
          outlined
        >
          {{ errorMsg }}
        </v-alert>
      </v-card-subtitle>
      <v-card-text class="mt-8">
        <v-form
          ref="form"
          v-model="valid"
          lazy-validation
        >
          <v-text-field
            v-model="email"
            label="Email"
            type="email"
            autofocus
            :rules="emailRules"
          />
          <v-text-field
            v-model="pwd"
            label="Password"
            type="password"
            :rules="pwdRules"
            @keydown.enter="login"
          />
        </v-form>
      </v-card-text>
      <v-card-actions class="ma-2">
        <v-btn
          block
          tile
          color="primary"
          @click="login"
        >
          LOG IN
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-row>
</template>
<script>
  import store from '@/store'
  import router, { routeName } from '@/router'
  import { errorMsg } from '@/assets/enums'
  import { firebaseAuth } from '@/firebase'
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'

  export default {
    name: 'LoginForm',
    data () {
      return {
        errorMsg: '',
        alertFlag: false,
        valid: true,
        email: '',
        emailRules: [
          v => !!v || '필수 항목입니다.',
          v => /.+@.+\..+/.test(v) || '이메일 형식이 맞지 않습니다.',
        ],
        pwd: '',
        pwdRules: [
          v => !!v || '필수 항목입니다.',
        ],
      }
    },
    methods: {
      async login () {
        if (this.empty(this.email)) {
          this.alertFlag = true
          this.errorMsg = errorMsg.CHECK_INPUT
          return
        }
        if (!this.valid) {
          return
        }
        await store.dispatch('setLoadingTrue')
        firebaseAuth.signInWithEmailAndPassword(this.email, this.pwd).then(async () => {
          const user = firebaseAuth.currentUser
          store.commit('setUid', user.uid)
          store.commit('setLoadingValue', 30)
          await axios.get(ApiList.roles).then(res => {
            store.commit('setRoles', res.data.data)
            store.commit('setLoadingValue', 60)
          })
          await axios.get(ApiList.adminWithId(user.uid)).then(res => {
            store.commit('setCurrentRole', res.data.data.role)
            store.commit('setLoadingValue', 100)
          })
          await store.dispatch('setLoadingFalse')
          await router.push({ name: routeName.DASHBOARD })
        }).catch(err => {
          this.pwd = ''
          this.alertFlag = true
          this.errorMsg = err
          store.dispatch('setLoadingFalse')
        })
      },
      empty (val) {
        return val.trim() === ''
      },
      validate () {
        this.$refs.form.validate()
      },
      reset () {
        this.$refs.form.reset()
      },
      resetValidation () {
        this.$refs.form.resetValidation()
      },
    },
  }
</script>
