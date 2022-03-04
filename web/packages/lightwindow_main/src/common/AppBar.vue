<template>
    <v-app-bar app color="white" height="30">
      <v-icon @click="$router.back()" dense color="blue">fal fa-arrow-square-left</v-icon>
      <v-spacer></v-spacer>
      <slot></slot>
      <v-spacer></v-spacer>
      <v-icon dense @click="closeClick" color="blue">fal fa-times-circle</v-icon>
    </v-app-bar>
</template>

<script lang="ts">
import Vue from 'vue'
import { Android } from '@/common/js/const'

export default Vue.extend({
  data() {
    return {
      Android
    }
  },
  methods: {
    closeClick() {
      if (Android) Android.close()
      else window.parent.postMessage('close', '*')
    }
  }
})
</script>

<style scoped>
</style>
