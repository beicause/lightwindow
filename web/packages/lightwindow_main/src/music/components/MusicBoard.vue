
<script setup lang="ts">
import { hideRipple, showRipple } from '@/common/js/ripple'
import { onMounted, onUnmounted, Ref, ref } from '@vue/composition-api'
import { isMobile } from '@/common/js/util'
import { player } from './soundPlayer'

const ripples: Ref<boolean[]> = ref(new Array(84).fill(false))
function show(key: number) {
  hide(key)
  const el = document.getElementById('__item' + key)
  if (!el) return
  showRipple(el, { class: 'blue--text' })
  ripples.value.splice(key, 1, true)
  player.value.stop()
  player.value.play((108 - key) + '', 0, { gain: 5, duration: 0.5 })
}

function hide(key: number) {
  const el = document.getElementById('__item' + key)
  el && ripples.value[key] && hideRipple(el)
  ripples.value.splice(key, 1, false)
}
const genKey = (col: number, row: number) => col + (row - 1) * 7
const eventOn = isMobile() ? null : 'mouseover'
const eventOff = isMobile() ? null : 'mouseout'

let itemWidth:number|undefined, itemHeight:number|undefined
onMounted(() => {
  const el = document.getElementById('__item1')
  itemWidth = el?.scrollWidth
  itemHeight = el?.scrollHeight
})

const lastKeys = new Map<number, number>()
const touchBoard = (e:TouchEvent) => {
  if (!(itemWidth && itemHeight)) return
  for (const t of e.targetTouches) {
    const x = t.pageX
    const y = t.pageY - document.getElementsByClassName('v-main')[0].scrollHeight
    const col = Math.ceil(x / itemWidth)
    const row = Math.ceil(y / itemHeight)
    const key = genKey(col, row)
    if (lastKeys.get(t.identifier) === key) return
    show(key)
    if (lastKeys.has(t.identifier)) {
      hide(lastKeys.get(t.identifier)!)
      lastKeys.delete(t.identifier)
    }
    lastKeys.set(t.identifier, key)
  }
}
document.body.addEventListener('touchmove', touchBoard)
document.body.addEventListener('touchstart', touchBoard)
document.body.addEventListener('touchend', (e:TouchEvent) => {
  for (const t of e.changedTouches) {
    if (lastKeys.has(t.identifier)) {
      hide(lastKeys.get(t.identifier)!)
      lastKeys.delete(t.identifier)
    }
  }
})
onUnmounted(() => {
  lastKeys.clear()
  document.body.removeEventListener('touchmove', touchBoard)
  document.body.removeEventListener('touchstart', touchBoard)
  document.body.removeEventListener('touchend', (e:TouchEvent) => {
    for (const t of e.changedTouches) {
      if (lastKeys.has(t.identifier)) {
        hide(lastKeys.get(t.identifier)!)
        lastKeys.delete(t.identifier)
      }
    }
  })
})
</script>

<template>
  <div class="flex flex-row fixed top-8 bottom-0 left-0 right-0" style="-webkit-user-select: none; user-select: none;">
    <div class="flex flex-col" v-for="col in 7" :key="col">
      <div
        :id="'__item' + genKey(col, row)"
        @[eventOn]="show(genKey(col, row))"
        @[eventOff]="hide(genKey(col, row))"
        class="item flex justify-center items-center blue--text"
        v-for="row in 12"
        :key="genKey(col, row)"
      >{{ ripples[genKey(col, row)] ? genKey(col, row) : '' }}</div>
    </div>
  </div>
</template>

<script lang='ts'>
export default {
  name: 'MusicBoard'
}
</script>
<style scoped>
.item {
  width: 14vw;
  height: 14vw;
}
</style>
