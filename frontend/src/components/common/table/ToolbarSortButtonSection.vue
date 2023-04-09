<template>
  <v-row
    class="ma-auto"
  >
    <v-divider
      class="mx-4"
      inset
      vertical
    />
    <v-btn
      color="primary"
      dark
      @click="handleDialogHandler(true)"
    >
      순서 배치
    </v-btn>
    <sorting-dialog
      :handler="dialogHandler"
      :table-header="header"
      :table-data="data"
      @stateSync="handleDialogHandler"
      @updateOrder="updateOrder"
    />
  </v-row>
</template>

<script>
  import SortingDialog from '@/components/common/SortingDialog'

  export default {
    name: 'SortButtonSection',
    components: {
      SortingDialog,
    },
    props: {
      label: String,
      header: Array,
      data: Array,
    },
    data: () => ({
      dialogHandler: false,
    }),
    computed: {
      buttonLabel () {
        return '순서 정렬'
      },
    },
    methods: {
      handleDialogHandler (bool) {
        this.dialogHandler = bool
      },
      updateOrder (data) {
        this.$emit('updateOrder', data)
      },
    },
  }
</script>
