<template>
  <v-btn
    min-width="30px"
    height="30px"
    class="header-item pa-0"
    :class="{ 'is-active': isActive ? isActive(): null }"
    :title="title"
    color="#555555"
    @click="action"
  >
    <v-icon>
      {{ icon }}
    </v-icon>
  </v-btn>
</template>

<script>
  export default {
    name: 'HeaderItem',
    props: {
      icon: {
        type: String,
        required: true,
      },

      title: {
        type: String,
        required: true,
      },

      action: {
        type: Function,
        required: true,
      },

      isActive: {
        type: Function,
        default: null,
      },
    },
  }
</script>

<style lang="scss" scoped>
.header-item {
  width: 1rem;
  height: 1rem;
  color: #FFFFFF;
  border: none;
  background-color: transparent;
  border-radius: 0.4rem;
  padding: 0.25rem;
  margin-right: 0.25rem;

  &.is-active,
  &:hover {
    color: #FFF;
    background-color: #0D0D0D;
  }
}
</style>
