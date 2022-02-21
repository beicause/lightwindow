<template>
  <view class="container">
    <view @click="previousDay" class="arrow-wrapper">
      <view class="left-arrow"></view>
    </view>
    <picker @change="dateChange" mode="multiSelector" :value="date" :range="[mon, day]">
      <text
        :style="{ color: activeDay === dayFormat(new Date()) ? '#2196f3' : '' }"
      >{{ pickerText }}</text>
    </picker>

    <view @click="nextDay" class="arrow-wrapper">
      <view class="right-arrow"></view>
    </view>
    <view @click="returnNow" class="return-today">
      <span>回到今天</span>
    </view>
  </view>
</template>

<script lang="ts">
import store from '../../store'
import Vue from "vue";
import { dayFormat, getEventDate } from '@/common/util';
import CalendarDay from "@/components/calendar/calendarDay.vue";

/**
 * 日程天的顶部日期组件
 * @see CalendarDay
 */

export default Vue.extend({
  name: "HeaderDate",
  data() {
    return {
      week: ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'],
    }
  },
  methods: {
    dayFormat,
    dateChange(e: any) {
      let [m, d]: [number, number] = (e.detail.value)
      m++
      d++
      const date = new Date()
      date.setMonth(2)
      date.setDate(0)
      if ((m === 4 || m === 6 || m === 9 || m === 11) && d > 30) return
      if (m === 2 && d > date.getDate()) return
      date.setMonth(m - 1)
      date.setDate(d)
      store.commit('setActiveDay', dayFormat(date))
    },
    returnNow() {
      const date = new Date()
      store.commit('setActiveDay', dayFormat(date))
    },
    nextDay() {
      const date = getEventDate(this.activeDay)
      date.setDate(date.getDate() + 1)
      store.commit('setActiveDay', dayFormat(date))
    },
    previousDay() {
      const date = getEventDate(this.activeDay)
      date.setDate(date.getDate() - 1)
      store.commit('setActiveDay', dayFormat(date))
    },
  },
  computed: {
    activeDay(): string {
      return store.state.activeDay
    },
    date(): [number, number] {
      const [, mm, dd] = this.activeDay.split('-') as [string, number, number]
      return [mm - 1, dd - 1]
    },
    pickerText(): string {
      const [, mm, dd] = this.activeDay.split('-')
      const d = getEventDate(this.activeDay)
      return mm + '月' + dd + '日' + this.week[d.getDay()]
    },
    mon(): string[] {
      const m: string[] = []
      for (let i = 1; i <= 12; i++) m.push(i + '月')
      return m
    },
    day(): string[] {
      const d: string[] = []
      for (let i = 1; i <= 31; i++) d.push(i + '日')
      return d
    }
  }
})
</script>

<style scoped>
.container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50px;
  border-bottom-color: #e5e5e5;
  border-bottom-style: solid;
  border-bottom-width: 1px;
}

.arrow-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 50px;
  height: 50px;
}

.left-arrow {
  transform: rotate(-45deg);
  width: 10px;
  height: 10px;
  border-left-color: #808080;
  border-left-style: solid;
  border-left-width: 2px;
  border-top-color: #555555;
  border-top-style: solid;
  border-top-width: 2px;
}

.right-arrow {
  transform: rotate(135deg);
  width: 10px;
  height: 10px;
  border-left-color: #808080;
  border-left-style: solid;
  border-left-width: 2px;
  border-top-color: #555555;
  border-top-style: solid;
  border-top-width: 2px;
}

.return-today {
  position: absolute;
  right: 0;
  top: 12px;
  padding: 0 5px 0 10px;
  height: 25px;
  line-height: 25px;
  font-size: 12px;
  border-top-left-radius: 25px;
  border-bottom-left-radius: 25px;
  color: #333;
  background-color: #f1f1f1;
}

text {
  font-size: 14px;
}
</style>
