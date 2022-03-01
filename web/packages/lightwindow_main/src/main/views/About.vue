<template>
  <v-container>
    <v-row no-gutters align="center">
      <v-col cols="3">App版本：</v-col>
      <v-col>{{ appVersionName }}</v-col>
      <v-col cols="1">
        <v-btn @click="update" :disabled="!Android" icon color="blue">
          <v-icon dense>fal fa-arrow-alt-circle-up</v-icon>
        </v-btn>
      </v-col>
    </v-row>
    <div class="pb-2">
      <v-divider></v-divider>
    </div>

    <v-row no-gutters align="center">
      <v-col cols="3">网页地址：</v-col>
      <v-col style="overflow: auto">{{ INDEX_URL }}</v-col>
      <v-col cols="1">
        <v-btn @click="copyOrNavigate(INDEX_URL)" icon color="blue">
          <v-icon dense>{{ Android ? 'fal fa-copy' : 'fal fa-location-arrow' }}</v-icon>
        </v-btn>
      </v-col>
    </v-row>
    <div class="pb-2">
      <v-divider></v-divider>
    </div>

    <v-row no-gutters align="center">
      <v-col cols="3">开源仓库：</v-col>
      <v-col style="overflow: auto">{{ GITHUB_URL }}</v-col>
      <v-col cols="1">
        <v-btn @click="copyOrNavigate(GITHUB_URL)" icon color="blue">
          <v-icon dense>{{ Android ? 'fal fa-copy' : 'fal fa-location-arrow' }}</v-icon>
        </v-btn>
      </v-col>
    </v-row>
    <div class="pb-2">
      <v-divider></v-divider>
    </div>

    <v-row no-gutters align="center">
      <v-col cols="3">建议反馈：</v-col>
      <v-col style="overflow: auto">{{ EMAIL }}</v-col>
      <v-col cols="1">
        <v-btn @click="copy(EMAIL)" icon color="blue">
          <v-icon dense>fal fa-copy</v-icon>
        </v-btn>
      </v-col>
    </v-row>
    <div class="pb-2">
      <v-divider></v-divider>
    </div>

    <v-row no-gutters align="center">
      <v-col cols="3">隐私政策：</v-col>
      <v-col style="overflow: auto">{{ INDEX_URL + '/policy' }}</v-col>
      <v-col cols="1">
        <v-btn @click="$router.push('/policy')" icon color="blue">
          <v-icon dense color="blue">fal fa-location-arrow</v-icon>
        </v-btn>
      </v-col>
    </v-row>
    <div class="pb-2">
      <v-divider></v-divider>
    </div>
  </v-container>
</template>
<script lang="ts">
import Vue from 'vue'
import { copy, getVersion, showPop } from '@/common/js/util'
import { Android, EMAIL, GITHUB_URL, INDEX_URL } from '@/common/js/const'

export default Vue.extend({
  name: 'About',
  data () {
    return {
      Android,
      GITHUB_URL,
      EMAIL,
      INDEX_URL,
      localAppVersion: '',
      appVersion: '',
      isAppUpdate: false
    }
  },
  mounted () {
    if (Android) {
      this.localAppVersion = Android.getAppVersion()
    }
    getVersion().then(res => {
      this.appVersion = res.data.app_version
      if (this.appVersion !== this.localAppVersion) this.isAppUpdate = true
    })
  },
  computed: {
    appVersionName (): string {
      if (!Android) return '---'
      return this.versionCodeToName(this.localAppVersion) +
        (this.isAppUpdate ? '（发现新版本' + this.versionCodeToName(this.appVersion) + '）' : '')
    }
  },
  methods: {
    versionCodeToName (code: string): string {
      return 'Ｖ' + code.split('').join('.')
    },
    copyOrNavigate (s: string, url?: string) {
      if (Android) {
        copy(s)
      } else {
        window.open(url || s)
      }
    },
    update () {
      if (Android && this.isAppUpdate) {
        Android.showVersionUpdate()
      } else {
        showPop('目前已是最新版本', 'success')
      }
    }
  }
})
</script>
<style scoped>
</style>
