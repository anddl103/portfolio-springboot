<template>
  <video
    :id="refName"
    :ref="refName"
    :class="height === 100 ? 'video-js-height-100' : 'video-js'"
    class="vjs-default-skin"
    controls
  >
    <source
      v-for="(source, i) in options.sources"
      :key="i"
      :src="source.src"
      :type="source.type"
    >
  </video>
</template>

<script>
  export default {
    name: 'VideoPlayer',
    props: {
      options: {
        type: Object,
        default () {
          return {}
        },
      },
      height: Number,
      refName: String,
    },
    data () {
      return {
        player: null,
        fileWidth: 0,
        fileHeight: 0,
      }
    },
    watch: {
      'options.sources' () {
        this.setDimensions()
      },
    },
    mounted () {
      this.setDimensions()
    },
    beforeDestroy () {
      if (this.player) {
        this.player.dispose()
      }
    },
    methods: {
      async setDimensions () {
        const target = document.getElementById(this.refName)
        const parent = this
        if (target !== null) {
          target.onloadedmetadata = () => {
            parent.sendDimensionInfo(target.videoWidth, target.videoHeight)
          }
        }
      },
      sendDimensionInfo (width, height) {
        this.$emit('dimension', { width: width, height: height })
      },
    },
  }
</script>
<style>
.vjs-big-play-button {
  background-color: transparent;
  font-size: 2em !important;
  background-repeat: no-repeat;
  background-size: 46px;
  background-position: 50% calc(50% - 10px);
  border: none !important;
}
.video-js {
  max-width: -webkit-fill-available;
  max-height: -webkit-fill-available;
  height: -webkit-fill-available;
}
</style>
