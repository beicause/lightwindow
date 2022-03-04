<template>
  <div>
    <app-bar>
      <v-icon dense color="blue">fal fa-music</v-icon>
      <div class="blue--text ml-1">音乐谱</div>
      <v-btn small icon @click="board = !board">
        <v-icon dense color="blue">{{ 'fal fa-' + (board ? 'bullseye-pointer' : 'bullseye-arrow') }}</v-icon>
      </v-btn>
    </app-bar>
    <v-btn
      color="blue"
      outlined
      class="fixed"
      style="left: 50%;top: 50%;transform: translate(-50%,-50%);"
      v-if="!isStart"
      @click="start"
    >点击开始</v-btn>
    <v-main v-if="isStart">
      <music-board v-if="board"></music-board>
      <music-score v-else></music-score>
    </v-main>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { Android } from '@/common/js/const'
import MusicScore from '@/music/components/MusicScore.vue'
import MusicBoard from './components/MusicBoard.vue'
import { audio, player, loadPlayer } from './components/soundPlayer'
import { disableScroll, resumeScroll } from '@/common/js/util'
import AppBar from '@/common/AppBar.vue'

export default Vue.extend({
  name: 'MusicApp',
  components: { MusicScore, MusicBoard, AppBar },
  data() {
    return {
      Android,
      board: true,
      isStart: false
    }
  },
  provide() {
    return {
      audio, player
    }
  },
  mounted() {
    loadPlayer()
    document.body.onclick = () => this.start()
  },
  beforeDestroy() {
    player && player.value.stop()
    audio && audio.close()
  },
  methods: {
    closeClick() {
      if (Android) Android.close()
      else window.parent.postMessage('close', '*')
    },
    start() {
      if (this.isStart) return
      player.value.play('0')
      this.isStart = true
    }
  },
  watch: {
    board: {
      immediate: true,
      handler(val: boolean) {
        if (val) disableScroll()
        else resumeScroll()
      }
    }
  }
})
</script>

<style scoped>
</style>
