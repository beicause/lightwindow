<template>
  <v-container>
    <v-textarea
      auto-grow
      :value="firstScore.inputValue"
      :label="levelName.get(firstScore.level)"
      :rules="rules"
      @input="e => onInput(firstScore)(e)"
      append-icon="fal fa-cog"
      @click:append="() => { blur(); activeSettingPicker = 0; $refs.picker.open() }"
    ></v-textarea>
    <v-textarea
      style="padding: 0"
      auto-grow
      :value="secondScore.inputValue"
      :label="levelName.get(secondScore.level)"
      :rules="rules"
      append-icon="fal fa-cog"
      @click:append="() => { blur(); activeSettingPicker = 1; $refs.picker.open() }"
      @input="e => onInput(secondScore)(e)"
    ></v-textarea>
    <v-slider
      dense
      :step="STEP"
      @change="sliderChange"
      :max="maxTime"
      :value="musicTime"
      :append-icon="isPlaying ? 'fal fa-pause' : 'fal fa-play'"
      @click:append="onPlay"
    ></v-slider>

    <music-guide class="pt-0"></music-guide>
    <setting-picker ref="picker">
      <setting-clipboard
        @pasted="onPasted"
        :copy-value="activeSettingScore.inputValue"
      ></setting-clipboard>
      <v-container>
        <setting-item
          @down-click="() => {
            activeSettingScore.level--
            if (activeSettingScore.level === -2) activeSettingScore.level = 1
            initMusic()
          }"
          @up-click="() => {
            activeSettingScore.level++
            if (activeSettingScore.level === 2) activeSettingScore.level = -1
            initMusic()
          }"
        >音调：1={{ activeSettingScore.level === 0 ? 'c1' : (activeSettingScore.level === 1 ? 'c2' : 'c') }}</setting-item>

        <setting-item
          @down-click="() => {
            activeSettingScore.gain--
            initMusic()
          }"
          @up-click="() => {
            activeSettingScore.gain++
            initMusic()
          }"
        >音量：{{ activeSettingScore.gain }}</setting-item>

        <setting-item
          @down-click="() => {
            beatDuration = 60 / (60 / beatDuration - 10)
            initMusic()
          }"
          @up-click="() => {
            beatDuration = 60 / (60 / beatDuration + 10)
            initMusic()
          }"
        >速度：{{ Math.floor(60 / beatDuration) }}</setting-item>
      </v-container>
    </setting-picker>
  </v-container>
</template>

<script lang="ts">
import Vue from 'vue'
import MusicGuide from '@/music/components/MusicGuide.vue'
import SettingItem from '@/music/components/SettingItem.vue'
import SettingPicker from '@/music/components/SettingPicker.vue'
import SettingClipboard from '@/music/components/SettingClipboard.vue'
import { showPop } from '@/common/js/util'
import { FIRST_SCORE, SECOND_SCORE, BEAT_DURATION, SECTION_BEATS, formatScore, sectionBeat, ScoreInfo } from './scoreUtil'

export default Vue.extend({
  name: 'MusicScore',
  components: {
    SettingClipboard,
    SettingPicker,
    SettingItem,
    MusicGuide
  },
  inject: ['player'],
  data () {
    return {
      FIRST_SCORE,
      SECOND_SCORE,
      BEAT_DURATION,
      SECTION_BEATS,
      // 每拍0.5秒
      beatDuration: 0.50,
      STEP: 0.0125,
      firstScore: {
        inputValue: '',
        level: 1,
        gain: 5
      } as ScoreInfo,
      secondScore: {
        inputValue: '',
        level: -1,
        gain: 5
      } as ScoreInfo,
      musicTime: 0,
      sliderValue: 0,
      maxTime: 0,
      isPlaying: false,
      activeSettingPicker: 0 as 0 | 1
    }
  },
  computed: {
    activeSettingScore (): ScoreInfo {
      if (this.activeSettingPicker === 0) return this.firstScore
      if (this.activeSettingPicker === 1) return this.secondScore
      throw new Error()
    },
    levelName (): Map<0 | 1 | -1, string> {
      const map = new Map<0 | 1 | -1, string>()
      map.set(1, '高音谱')
      map.set(0, '中音谱')
      map.set(-1, '低音谱')
      return map
    },
    rules (): ((value: string) => boolean | string)[] {
      return [
        // (value: string) => !(/uuuu/.test(value) && value[value.length - 1] !== '1') || 'uuuu后只能为1',
        // (value: string) => !(/[ud#]/.test(value) && !/\d/.test(value[value.length - 1])) || '变音记号后请接音符',
        (value: string) => (/^[0-7\r\n/_~ud#]*$/.test(value) || !value) || '出现非法字符',
        (value: string) => !/_{3,}/.test(value) || '不能小于十六分音符',
        (value: string) => (!/^~/.test(value) && /([\d~]~)*/.test(value)) || '增时线前接数字',
        // (value: string) => !/_#*\.*[0-7]?\.*\-/.test(value) || '增时线和减时线不可共用',
        (value: string) => {
          let error = false
          const sections = value.split('/')
          sections.forEach(r => {
            if (this.sectionBeat(r) > this.SECTION_BEATS) error = true
          })
          return (!error) || '每小节不超过四拍'
        }
      ]
    }
  },
  methods: {
    initMusic () {
      this.onInput(this.firstScore)(this.firstScore.inputValue)
      this.onInput(this.secondScore)(this.secondScore.inputValue)
    },
    onInput (score: ScoreInfo) {
      return (value: string) => {
        this.isPlaying = false
        this.player.value.stop()
        // 如果长度增加
        if (value.length > score.inputValue.length) {
          const info = this.lastNoteInfo(value, score.level)
          if (info) {
            this.player.value.play(info.note, 0, {
              duration: info.duration,
              gain: score.gain
            })
          }
        }
        const sections = value.split('/')
        if (this.sectionBeat(sections[sections.length - 1]) === this.SECTION_BEATS) value += '/'
        let musicLength = 0
        sections.forEach(s => {
          musicLength += this.sectionBeat(s) * this.beatDuration
        })
        this.maxTime = musicLength
        this.sliderValue = 0
        this.musicTime = 0
        score.inputValue = value
      }
    },

    onPlay () {
      this.firstScore.inputValue = this.formatScore(this.firstScore.inputValue)
      this.secondScore.inputValue = this.formatScore(this.secondScore.inputValue)
      if (this.isPlaying) {
        this.player.value.stop()
        this.isPlaying = false
        return
      }
      if (this.musicTime >= this.maxTime) this.musicTime = 0
      const getSchedule = (score: ScoreInfo) => {
        let maxTime = 0
        // 获取音符序列
        const schedule = [] as { time: number, note: string, duration: number, gain: number }[]
        const input = score.inputValue
        if (!input) return schedule
        for (let i = 0; i <= input.length; i++) {
          const noteString = input.substring(0, i)
          if (input[i] === '~') continue
          const info = this.lastNoteInfo(noteString, score.level)
          if (!info) continue
          schedule.push({
            time: maxTime,
            note: info.note,
            duration: info.duration,
            gain: score.gain
          })
          maxTime += info.duration
        }
        // 剔除时间小于滑块值的
        for (let i = 0; i < schedule.length; i++) {
          schedule[i].time -= this.musicTime
          if (schedule[i].time < 0) {
            schedule.splice(i, 1)
            i--
          }
        }
        if (maxTime > this.maxTime) this.maxTime = maxTime
        return schedule
      }
      const schedule = [...getSchedule(this.firstScore), ...getSchedule(this.secondScore)]
      if (schedule.length === 0) return
      // console.log(schedule)
      this.player.value.schedule(0, schedule)
      this.isPlaying = true
      // webview does not support high frame rate animation
      const step = 0.1
      const timerId = setInterval(() => {
        if (!this.isPlaying) clearInterval(timerId)
        this.musicTime += step
        if (this.musicTime >= this.maxTime) {
          clearInterval(timerId)
          this.onPlay()
          this.musicTime = 0
          setTimeout(() => this.onPlay(), 300)
        }
      }, step * 1000)
    },

    sliderChange (value: number) {
      this.player.value.stop()
      this.isPlaying = false
      this.musicTime = value
    },
    lastNoteInfo (s: string, level: 1 | 0 | -1): { note: string, duration: number } | null {
      const lastChar = s[s.length - 1]
      // 不是音符直接返回
      if (!['0', '1', '2', '3', '4', '5', '6', '7', '~'].includes(lastChar)) return null
      const noteIndexReversed = [] as number[]
      for (let i = s.length - 1; i >= 0; i--) {
        if (/\d/.test(s[i])) noteIndexReversed.push(i)
        if (noteIndexReversed.length === 2) break
      }
      const lastNoteIndex = noteIndexReversed[0]
      const lastSecondNoteIndex = noteIndexReversed[1]
      const lastNote = s[lastNoteIndex]
      let level1 = 0
      let duration = this.beatDuration
      let isSharpe = 0

      // 解析前置符号
      const sign = s.substring(lastSecondNoteIndex + 1, lastNoteIndex)
      for (let i = 0; i < sign.length; i++) {
        if (sign[i] === 'u') level1++
        if (sign[i] === 'd') level1--
        if (sign[i] === '#') isSharpe = 1
        if (sign[i] === '_') duration /= 2
      }
      // 解析增时线
      if (lastChar === '~') duration += (s.length - 1 - lastNoteIndex) * this.beatDuration
      if (duration > 2) duration = 2

      if (lastChar === '0') {
        return {
          note: '0',
          duration
        }
      }
      const note = (lastNote as unknown as number << 0) + 59 + level1 * 12 + isSharpe + 12 * level
      return {
        note: '' + note,
        duration
      }
    },
    onPasted (clipValue: string) {
      if (clipValue) {
        this.activeSettingScore.inputValue += clipValue
        showPop('剪切板粘贴成功', 'success')
      } else {
        showPop('剪切板为空')
      }
    },
    blur () {
      for (const element of document.getElementsByTagName('textarea')) {
        element.blur()
      }
    },
    sectionBeat,
    formatScore
  },
  mounted () {
    const duration = Number.parseFloat(localStorage.getItem(this.BEAT_DURATION) || '0.50')
    const s1 = localStorage.getItem(this.FIRST_SCORE)
    const s2 = localStorage.getItem(this.SECOND_SCORE)
    if (duration) this.beatDuration = duration
    this.firstScore = (s1 ? JSON.parse(s1) : {
      inputValue: '',
      level: 1,
      gain: 5
    }) as ScoreInfo
    this.secondScore = (s2 ? JSON.parse(s2) : {
      inputValue: '',
      level: -1,
      gain: 5
    }) as ScoreInfo
  },
  watch: {
    beatDuration (val: number) {
      localStorage.setItem(this.BEAT_DURATION, '' + val)
    },
    firstScore: {
      deep: true,
      handler (val: ScoreInfo) {
        // console.log(val)
        localStorage.setItem(this.FIRST_SCORE, JSON.stringify(val))
      }
    },
    secondScore: {
      deep: true,
      handler (val: ScoreInfo) {
        // console.log(val)
        localStorage.setItem(this.SECOND_SCORE, JSON.stringify(val))
      }
    }
  },
  beforeDestroy () {
    this.isPlaying = false
    this.player.value.stop()
  }
})
</script>

<style scoped>
</style>
