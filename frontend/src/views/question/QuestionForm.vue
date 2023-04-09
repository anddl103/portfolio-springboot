<template>
  <v-container
    class="pa-4"
    fluid
  >
    <v-row class="ma-auto">
      <v-col
        cols="1"
      >
        문의 유저
      </v-col>
      <v-col cols="10">
        <v-row>
          <v-card
            solo
            class="ml-8"
          >
            <v-card-title>
              {{ targetItem.createdBy }}
            </v-card-title>
          </v-card>
        </v-row>
      </v-col>
    </v-row>
    <v-divider
      class="mt-3 mb-10"
    />
    <v-row class="ma-auto">
      <v-col
        cols="1"
      >
        문의 내용
      </v-col>
      <v-col cols="10">
        <h4 class="ml-8">
          제목 : {{ targetItem.title }}
        </h4>
        <v-card-text>
          <editor
            v-model="targetItem.content"
            :read-only="true"
          />
        </v-card-text>
      </v-col>
    </v-row>
    <v-divider
      v-if="handleShowAnswer"
      class="mt-3 mb-10"
    />
    <v-row
      v-if="handleShowAnswer"
      class="ma-auto"
    >
      <v-col
        cols="1"
      >
        답변
      </v-col>
      <v-col cols="10">
        <v-row
          class="mx-4"
        >
          <editor
            v-model="targetItem.answer"
            :read-only="readOnly"
            :attach-image="true"
            :has-error="editorHasError"
            @setHasError="handleEditorError"
          />
        </v-row>
      </v-col>
    </v-row>
    <v-divider
      class="mt-3 mb-10"
    />
    <v-row
      v-if="readOnly"
      class="ma-auto"
      justify="center"
    >
      <v-btn
        @click="toList"
      >
        목록
      </v-btn>
      <v-btn
        color="primary"
        @click="toAnswer"
      >
        답변
      </v-btn>
    </v-row>
    <v-row
      v-else
      class="ma-auto 7"
      justify="center"
    >
      <v-btn
        color="primary"
        @click="answer"
      >
        저장
      </v-btn>
      <v-btn
        @click="cancelModify"
      >
        취소
      </v-btn>
    </v-row>
  </v-container>
</template>
<script>
  import axios from '@/axios'
  import ApiList from '@/axios/api-list'
  import router, { routeName } from '@/router'
  import commonErrorMixin from '@/mixins/commonErrorMixin'
  import Editor from '@/components/common/editor/TipTapEditor'
  import store from '@/store'
  import editorMixin from '@/mixins/editorMixin'
  import commonFormMixin from '@/mixins/commonFormMixin'

  export default {
    name: 'QuestionForm',
    components: {
      Editor,
    },
    mixins: [
      commonErrorMixin,
      editorMixin,
      commonFormMixin,
    ],
    data: () => ({
      readOnly: true,
      targetItem: JSON.parse(JSON.stringify(store.state.common.targetItem)),
      answered: false,
    }),
    computed: {
      handleShowAnswer () {
        if (this.answered) {
          return true
        }
        if (!this.readOnly) {
          return true
        }
        return false
      },
    },
    mounted () {
      this.initialize()
    },
    methods: {
      initialize () {
        this.setInitItems()
      },
      setInitItems () {
        if (this.targetIndex !== -1) {
          if (this.targetItem.answer) this.answered = true
        } else {
          this.readOnly = false
        }
      },
      cancelModify () {
        this.targetItem = JSON.parse(JSON.stringify(this.defaultItem))
        this.handleEditorError(false)
        this.readOnly = true
      },
      async toAnswer () {
        this.readOnly = false
      },
      async toList () {
        await router.push({ name: routeName.QUESTION })
      },
      validation () {
        const condition = this.checkEditForm(this.targetItem.answer)
        return condition
      },
      async answer () {
        if (!this.validation()) return
        await store.dispatch('setLoadingTrue')
        const req = {
          answer: this.targetItem.answer,
        }
        axios.patch(ApiList.questionWithId(this.targetItem.id), req).then(res => {
          if (!this.validateResponse(res)) return
          this.defaultItem = res.data.data
          this.answered = true
          this.readOnly = true
        }).catch(err => {
          this.setError(err)
        })
      },
    },
  }
</script>
<style>
.width-webkit-fill {
  width: -webkit-fill-available;
}
</style>
