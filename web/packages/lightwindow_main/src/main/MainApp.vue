<template>
  <div>
    <v-app-bar style="z-index: 7" height="30" app extension-height="35" color="white">
      <template v-slot:default>
        <v-icon id="icon-zoom" size="18" :style="{visibility:zoom?'visible':'hidden'}" @click="zoomClick"
                color="blue" style="border: #2196f3 solid 1px;">
          mdi mdi-arrow-top-left-bottom-right border
        </v-icon>
        <div style="margin: 0 auto;display: flex;justify-content: center;align-items: center">
          <img height="20" width="20" src="/images/logo.svg" alt="logo"/>
          <span class="blue--text" style="font-family: source-han, Roboto, sans-serif !important;">窗隙流光</span>
        </div>
        <v-icon id="icon-close" dense @click="closeClick" color="blue">
          fal fa-times-circle
        </v-icon>
      </template>

      <template v-slot:extension>
        <v-tabs grow>
          <v-tab :to="{name:'feature'}">
            <v-icon>mdi-database-outline</v-icon>
            <span>功能</span>
          </v-tab>
          <v-tab :to="{name:'guide'}">
            <v-icon>mdi-script-text-outline</v-icon>
            <span>说明</span>
          </v-tab>
          <v-tab :to="{name:'about'}">
            <v-icon>mdi-information-outline</v-icon>
            <span>关于</span>
          </v-tab>
        </v-tabs>
      </template>
    </v-app-bar>
    <v-main>
      <router-view></router-view>
    </v-main>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { Android } from '@/common/js/const'

export default Vue.extend({
  name: 'MainApp',
  data () {
    return {
      zoom: true,
      Android
    }
  },
  created () {
    window.showZoom = () => {
      this.zoom = true
    }
  },
  methods: {
    closeClick () {
      if (Android) {
        Android.close()
      } else {
        window.parent.postMessage('close', '*')
      }
    },
    zoomClick () {
      if (Android) {
        this.zoom = false
        Android.showZoom()
      } else {
        window.parent.postMessage('zoom', '*')
      }
    }
  }
})
</script>
