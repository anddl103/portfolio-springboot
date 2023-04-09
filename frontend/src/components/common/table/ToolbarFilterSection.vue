<template>
  <v-row>
    <v-divider
      class="mx-4"
      inset
      vertical
    />
    <v-autocomplete
      :items="data"
      item-text="name"
      item-value="id"
      :label="label"
      small-chips
      clearable
      deletable-chips
      hide-details
      @change="update"
    >
      <template v-slot:selection="{ item }">
        <v-row
          v-if="isCategory"
          class="mx-auto my-3 max-width-fit-contents"
        >
          {{ getCategoryListingText(item) }}
        </v-row>
        <v-row
          v-if="isArtist"
          class="mx-auto my-3"
          align="center"
        >
          <v-avatar
            left
          >
            <v-img
              class="my-2"
              :src="artistGetUrl(item)"
              :aspect-ratio="4/3"
              max-width="70"
              max-height="70"
            />
          </v-avatar>
          <v-divider
            class="mx-4"
            inset
            vertical
          />
          {{ item.artist ? item.artist.name : item.name }}
        </v-row>
      </template>
      <template
        v-slot:item="{ item }"
      >
        <v-row v-if="isCategory">
          {{ getCategoryListingText(item) }}
        </v-row>
        <v-row
          v-if="isArtist"
          class="mx-auto my-3"
          align="center"
        >
          <v-avatar
            left
          >
            <v-img
              class="my-2"
              :src="artistGetUrl(item)"
              :aspect-ratio="4/3"
              max-width="70"
              max-height="70"
            />
          </v-avatar>
          <v-divider
            class="mx-4"
            inset
            vertical
          />
          {{ item.artist ? item.artist.name : item.name }}
        </v-row>
      </template>
    </v-autocomplete>
  </v-row>
</template>

<script>
  import s3GetUrlMixin from '@/mixins/s3GetUrlMixin'

  export default {
    name: 'FilterSection',
    components: {},
    mixins: [
      s3GetUrlMixin,
    ],
    props: {
      model: String,
      data: Array,
      title: String,
    },
    computed: {
      label () {
        return this.title + ' 필터'
      },
      isArtist () {
        return this.title === '아티스트'
      },
      isCategory () {
        return this.title === '카테고리'
      },
    },
    methods: {
      getCategoryListingText (item) {
        const up = item.localeCodeSubject
        if (up.ko !== undefined) {
          return up.ko.subject
        } else {
          return up.en.subject
        }
      },
      async update (val) {
        this.$emit('update:model', val)
      },
      artistGetUrl (item) {
        const key = item.artist === undefined ? item.thumbnailKey : item.artist.logoKey
        return this.getSignedUrl(key)
      },
    },
  }
</script>
<style>
</style>
