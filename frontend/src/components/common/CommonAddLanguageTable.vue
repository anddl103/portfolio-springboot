<template>
  <v-row
    class="ma-auto"
    justify="center"
    align-content="center"
  >
    <v-col
      cols="2"
      :class="titleAlign"
    >
      <v-sheet
        height="100%"
        class="tableSheet"
      >
        {{ tableTitle }}
        <v-btn
          v-if="multiple && !readonly"
          color="primary"
          dark
          block
          class="ma-auto"
          @click="handleBtn"
        >
          <h3>{{ modify ? '추가' : '내용 수정' }}</h3>
        </v-btn>
      </v-sheet>
    </v-col>
    <v-col>
      <v-data-table
        class="width-webkit-fill"
        :headers="tableHeader"
        :items="tableData"
        :hide-default-header="hideHeader"
        :hide-default-footer="true"
      >
        <template
          v-if="asHeader"
          v-slot:header="{ props }"
        >
          <v-row class="ma-auto">
            <v-col
              v-for="(head, i) in props.headers"
              :key="i"
              class="text-align-center"
            >
              {{ head.text }}
            </v-col>
          </v-row>
        </template>
        <template v-slot:body="{ items }">
          <draggable
            :list="items"
            direction="h"
            tag="tbody"
            class="width-webkit-fill"
            v-bind="{disabled : !modify}"
            @end="syncSort(items)"
          >
            <tr
              v-for="(item, k) in items"
              :key="k"
            >
              <td
                v-for="(column, i) in tableHeader"
                :key="i"
                class="text-align-center"
              >
                <v-icon
                  v-if="column.value === 'order'"
                  :disabled="!modify"
                >
                  mdi-arrow-all
                </v-icon>
                <v-spacer v-else-if="column.value === 'actions' && !modify" />
                <v-btn
                  v-else-if="column.value === 'actions' && modify"
                  color="primary"
                  dark
                  class="ma-auto"
                  fab
                  x-small
                  :disabled="readonly"
                  @click="removeRow(k)"
                >
                  <v-icon>
                    mdi-minus
                  </v-icon>
                </v-btn>
                <v-text-field
                  v-else
                  v-model="item.values[column.value]"
                  :rules="rules"
                  :readonly="modify === false"
                  @focusin="focusIn(item.values[column.value])"
                  @focusout="focusOut(item.values[column.value])"
                />
              </td>
            </tr>
          </draggable>
        </template>
      </v-data-table>
    </v-col>

    <common-confirm-dialog
      :dialog-handler.sync="confirmDialog"
      :dialog-content="confirmDialogContent"
      @confirm="confirmSubmit"
    />
  </v-row>
</template>

<script>
  import CommonConfirmDialog from '@/components/common/CommonConfirmDialog'
  import commonConfirmMixin from '@/mixins/commonConfirmMixin'
  import draggable from 'vuedraggable'

  export default {
    name: 'CommonAddLanguageTable',
    components: {
      CommonConfirmDialog,
      draggable,
    },
    mixins: [
      commonConfirmMixin,
    ],
    props: {
      tableTitle: String,
      tableData: Array,
      tableHeader: Array,
      multiple: { type: Boolean, default: true },
      readonly: { type: Boolean, default: false },
      rules: Array,
      hideHeader: { type: Boolean, default: false },
      asHeader: { type: Boolean, default: false },
      titleAlign: { type: String, default: '' },
    },
    data: () => ({
      items: [],
      oldValue: '',
      modify: false,
    }),
    watch: {
      readonly (newval) {
        if (!this.multiple) {
          this.modify = !newval
        } else {
          if (newval) {
            this.modify = false
          }
        }
      },
      tableData: {
        async handler () {
          await this.init()
          if (this.multiple && this.tableData[0].id === '') {
            this.allowModify()
          }
        },
      },
      tableHeader () {
        if (this.multiple) {
          if (!this.tableHeader.find(h => h.value === 'order')) {
            this.tableHeader.splice(0, 0, {
              text: '순서',
              value: 'order',
              sortable: false,
              width: 70,
            })
          }
        }
      },
    },
    created () {
      this.init()
    },
    methods: {
      confirmSubmit () {
        this.confirmDialog = false
        this.allowModify()
      },
      allowModify () {
        this.modify = true
      },
      handleBtn () {
        if (this.modify) {
          this.addRow()
        } else {
          const warnStr = '멤버 수정시 해당 아티스트와 관련이 있는 카드의 멤버필터를 꼭 확인해야 합니다.<br /> <br /> 정말 수정 하시겠습니까?'
          this.openConfirmDialog(warnStr)
        }
      },
      focusIn (item) {
        this.oldValue = item
      },
      focusOut (item) {
        this.$emit('inputHandler', item, this.oldValue)
      },
      async init () {
        if (this.tableData && this.tableData.length < 1) {
          this.addRow()
        }
      },
      addRow () {
        this.$emit('addRow', this.tableData)
      },
      removeRow (idx) {
        this.$emit('removeRow', this.tableData, idx)
      },
      syncSort (arr) {
        this.$emit('sort', arr)
      },
    },
  }
</script>
<style>
.tableSheet {
  background-color: inherit !important;
}
</style>
