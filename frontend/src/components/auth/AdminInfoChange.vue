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
        수정
      </v-btn>
    </template>
    <v-card>
      <v-card-title class="text-h5 accent white--text">
        사용자 정보 수정
      </v-card-title>
      <v-card-text>
        <v-form v-model="valid">
          <v-container fluid>
            <v-row>
              <v-text-field
                v-model="email"
                label="E-mail"
                disabled
              />
            </v-row>
            <v-row>
              <v-text-field
                v-model="nickName"
                label="nickname"
                :rules="[rules.required]"
              />
            </v-row>
            <v-row>
              <v-text-field
                v-model="authority"
                label="authority"
                disabled
              />
            </v-row>
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
          @click="patchUser(nickName)"
        >
          저장
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script>
  export default {
    name: 'AdminInfoChange',
    data () {
      return {
        dialog: false,
        valid: false,
        rules: {
          required: (value) => !!value || 'Required.',
        },
      }
    },
    methods: {
      async patchUser (nickName) {
        console.log('------------- patchUser method -------------')
        this.dialog = false
        await this.$store.dispatch('patchUser', { nickName })
      },
    },
  }
</script>
