<template>
  <div class="pa-3">
    <div class="d-flex justify-space-between align-center">
      <div class="flex-center">
        <img
          :height="isMobile ? 32 : 48"
          :width="isMobile ? 32 : 48"
          src="/images/logo.svg"
          alt="logo"
        />
        <span
          style="font-family: source-han, Roboto, sans-serif !important;"
          class="blue--text text-sm-h4 text-h5"
        >窗隙流光</span>
      </div>
      <v-icon @click="toGithub" :large="!isMobile">fab fa-github</v-icon>
    </div>

    <div style="text-align: center;vertical-align: middle">
      新颖·创意·简洁
      <br />工具优先的悬浮窗应用
    </div>
    <div style="height: 65vh;" class="flex justify-center items-center">
      <img
        v-if="isClose"
        @click="isClose = false"
        class="anim-logo"
        height="60"
        width="60"
        src="/images/logo.svg"
        alt="logo"
      />
      <iframe
        class="iframe-normal"
        src="../main/"
        :class="{ 'iframe-hide': isClose, 'iframe-full': isZoom && !isClose, 'blue-border': !isMobile || !isZoom }"
      ></iframe>
    </div>
    <!-- <div style="position: absolute;top: 85%;left: 0;right: 0"> -->
    <div style="height: 15vh;" class="d-flex justify-center items-center">
      <v-btn
        :disabled="!downloadUrl"
        @click="download"
        style="text-transform: none;"
        :x-large="!isMobile"
        color="blue"
        outlined
      >
        <v-icon>mdi-android</v-icon>Android下载
      </v-btn>
    </div>
    <v-row no-gutters justify="center">
      <v-col sm="5" cols="11" class="ma-3" v-for="(item,index) in cards" :key="index">
        <v-card>
          <div class="d-flex flex-row justify-space-between pr-4">
            <div>
              <v-card-title>{{ item.title }}</v-card-title>
              <v-card-text>{{ item.text }}</v-card-text>
            </div>
            <v-icon>{{ item.icon }}</v-icon>
          </div>
        </v-card>
      </v-col>
    </v-row>
    <div class="mx-4 my-2">
      遇到问题？请联系：
      <br />QQ群：852708330
      <br />Email：1494181792@qq.com
    </div>
    <div style="font-size: 12px;" class="d-flex align-center justify-center flex-column">
      <a style="color: #bbb" href="https://beian.miit.gov.cn/">赣ICP备2021005447号</a>
      <div>
        <img src="/images/gov.png" alt="gov" />
        <a
          style="color: #bbb"
          href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=36070302360949"
        >赣公网安备36070302360949号</a>
      </div>
    </div>
  </div>
  <!-- </div> -->
</template>

<script lang="ts">
import Vue from 'vue'
import { GITHUB_URL, INDEX_URL } from '@/common/js/const'
import { disableScroll, getVersion, resumeScroll } from '@/common/js/util'

class Card {
  title: string
  text: string
  icon: string

  constructor(title: string, text: string, icon: string) {
    this.title = title
    this.text = text
    this.icon = icon
  }
}

export default Vue.extend({
  name: 'IndexApp',
  data() {
    return {
      isZoom: false,
      isClose: true,
      downloadUrl: '',
      cards: [
        new Card('日程表', '通知浮窗，便捷查看，支持导入教务课表', 'fal fa-calendar-week'),
        new Card('音乐谱', '音符弹响指间', 'fal fa-music')
      ]
    }
  },
  computed: {
    isMobile(): boolean {
      return this.$vuetify.breakpoint.mobile
    }
  },
  watch: {
    isClose(val: boolean) {
      if (val) resumeScroll()
      else if (this.isZoom) disableScroll()
    },
    isZoom(val: boolean) {
      if (val) disableScroll()
      else resumeScroll()
    }
  },
  mounted() {
    window.addEventListener('message', e => {
      if (e.data === 'zoom') this.isZoom = !this.isZoom
      if (e.data === 'close') this.isClose = !this.isClose
    })
    getVersion().then(res => {
      this.downloadUrl = INDEX_URL + '/lightwindow-v' + res.data.app_version.split('').join('.') + '.apk'
    }).catch(err => {
      console.log(err)
    })
  },
  methods: {
    download() {
      window.open(this.downloadUrl)
    },
    toGithub() {
      window.open(GITHUB_URL)
    }
  }
})
</script>

<style scoped>
@import '../../public/css/font.css';
.iframe-hide {
  width: 0 !important;
  height: 0 !important;
  transition-property: width, height;
  transition-duration: 0.5s;
}
.iframe-full {
  position: fixed;
  top: 0;
  bottom: 0;
  margin: auto;
  height: 100vh !important;
  max-height: 100%;
}
.blue-border {
  border: #2196f3 1px solid;
}
.iframe-normal {
  transition-property: width, height;
  transition-duration: 0.5s;
  background-color: white;
  z-index: 1;
  height: 65vh;
  width: 100%;
  max-width: 900px;
}

.flex-center {
  display: flex;
  justify-content: center;
  align-items: center;
}

.absolute-center {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  margin: auto;
}

.anim-logo {
  animation-name: logoAnim;
  animation-iteration-count: infinite;
  animation-duration: 4s;
  animation-timing-function: linear;
}

@keyframes logoAnim {
  0% {
    transform: rotate(30deg);
  }
  50% {
    transform: rotate(210deg);
  }
  100% {
    transform: rotate(390deg);
  }
}
</style>
