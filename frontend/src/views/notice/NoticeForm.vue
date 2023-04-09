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

    <v-row class="ma-auto">
      <v-col
        cols="1"
      >
        공지사항
      </v-col>
      <v-col cols="10">
        <v-form
          ref="form"
          v-model="formValid"
          lazy-validation
        >
          <v-row>
            <v-text-field
              v-model="targetItem.noticeAt"
              :class="!editMode ? 'disable-events' : ''"
              label="공지일시 (UTC + 9)"
              :rules="[required, timeFormat]"
              :readonly="!editMode"
              @click="handleDatetimePicker"
            />
          </v-row>
          <subject-contents-component
            v-for="lc in localeCodes"
            :key="lc.code"
            :locale-code="lc"
            :default-item.sync="targetItem.localeCodeContents[lc.code]"
            :is-new="defaultItem.id === undefined"
            :read-only="!editMode"
            :reset="resetEditComponent"
            @resetCallback="syncResetState"
          />
        </v-form>
      </v-col>
    </v-row>
    <v-divider
      class="mt-3 mb-10"
    />
    <button-handler
      :cancel-destination="cancelDestination"
      :edit-mode="editMode"
      :target-index="targetIndex"
      @save="save"
      @update="update"
      @modify="toEditMode"
      @cancelModify="cancelModify"
    />
    <datetime
      ref="datetimePicker"
      v-model="pickerDateTime"
      class="width-webkit-fill"
      input-class="datetimeInput"
      type="datetime"
      format="yyyy-MM-dd HH:mm"
      hidden
      value-zone="Asia/Seoul"
      :minute-step="15"
      :week-start="7"
      :min-datetime="minDate"
    />
  </v-container>
</template>
<script >
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import router, { routeName } from '@/router'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import CommonErrorAlert from '@/components/common/CommonErrorAlert'
  import SubjectContentsComponent from '@/components/common/editor/SubjectContentsComponent'
  import ButtonHandler from '@/components/common/ButtonHandler'
  import store from '@/store'
  import { Datetime } from 'vue-datetime'
  import 'vue-datetime/dist/vue-datetime.css'
  import editorMixin from '@/mixins/editorMixin'
  import rulesMixin from '@/mixins/rulesMixin'
  import commonFormMixin from '@/mixins/commonFormMixin'
  import moment from 'moment-timezone'

  export default {
    name: 'NoticeForm',
    components: {
      ButtonHandler,
      CommonErrorAlert,
      SubjectContentsComponent,
      datetime: Datetime,
    },
    mixins: [
      commonErrorMixin,
      rulesMixin,
      editorMixin,
      commonFormMixin,
    ],
    data: () => ({
      readOnly: true,
      editMode: false,
      formValid: true,
      cancelDestination: routeName.NOTICE,
      targetItem: JSON.parse(JSON.stringify(store.state.common.targetItem)),
      pickerDateTime: '',
    }),
    computed: {
      minDate () {
        return moment().format()
      },
    },
    watch: {
      pickerDateTime () {
        this.targetItem.noticeAt = moment(this.pickerDateTime).format('YYYY-MM-DD HH:mm')
      },
    },
    created () {
      this.initialize()
    },
    methods: {
      initialize () {
        this.setInitItems()
      },
      async setInitItems () {
        if (this.targetIndex !== -1) {
          this.targetItem = JSON.parse(JSON.stringify(this.defaultItem))
          for (const lc of this.localeCodes) {
            this.targetItem.localeCodeContents[lc.code].isValid = true
          }
        } else {
          this.createEmptyTargetItem()
          this.toEditMode()
        }
      },
      createEmptyTargetItem () {
        const obj = {}
        for (const lc of this.localeCodes) {
          obj[lc.code] = {
            isValid: true,
          }
        }
        this.targetItem = {
          id: '',
          localeCodeContents: obj,
        }
      },
      handleDatetimePicker (event) {
        if (this.editMode) {
          this.$refs.datetimePicker.open(event)
        }
      },
      cancelModify () {
        this.handleEditorError(false)
        this.resetTarget()
        this.toReadOnly()
      },
      toEditMode () {
        this.editMode = true
      },
      toReadOnly () {
        this.editMode = false
      },
      async toList () {
        await router.push({ name: routeName.NOTICE })
      },
      validation () {
        // 공지일시 체크
        const con1 = this.checkForm(this.$refs.form)
        // editors 체크
        const conArr = []
        for (const lc of this.localeCodes) {
          conArr.push(this.targetItem.localeCodeContents[lc.code].isValid)
        }
        return con1 === true && conArr.findIndex(c => c === false) === -1
      },
      pickerTimeToNoticeTime (req) {
        // kst to utc
        if (this.pickerDateTime === '') return
        req.noticeAt = moment.utc(this.pickerDateTime).format()
      },
      update () {
        if (!this.validation()) return
        store.dispatch('setLoadingTrue')
        const req = JSON.parse(JSON.stringify(this.targetItem))
        this.pickerTimeToNoticeTime(req)
        axios.patch(ApiList.noticeWithId(this.targetItem.id), req).then(res => {
          if (!this.validateResponse(res)) return
          this.defaultItem = res.data.data
          this.toReadOnly()
        }).catch(err => {
          this.setError(err)
        })
      },
      save () {
        if (!this.validation()) return
        store.dispatch('setLoadingTrue')
        const req = JSON.parse(JSON.stringify(this.targetItem))
        this.pickerTimeToNoticeTime(req)
        for (const lc of this.localeCodes) {
          delete req.localeCodeContents[lc.code].isValid
        }
        axios.post(ApiList.notices, req).then(res => {
          if (!this.validateResponse(res)) return
          this.toList()
        }).catch(err => {
          this.setError(err)
        })
      },
    },
  }
</script>
<style>
.datetimeInput {
  width: inherit;
  background-color: #999999;
}
</style>
