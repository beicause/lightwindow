<template>
  <view style="display: flex;height: 110px;width: 365px;">
    <view v-for="(item,index) in dayEvents" :key="index" class="event-item"
          :style="{left: x(item.time)+'px',color:item.color}">
      <text style="font-size: 12px;height: 14px">{{ item.time.substr(0, 5) }}</text>
      <view style="display: flex;height: 96px;">
        <text class="event-text">{{ item.title }}</text>
        <view class="vertical-line" :style="{backgroundColor: item.alarm.match(/^\*/) ? item.color : ''}"></view>
        <text class="event-text">{{ item.detail }}</text>
      </view>
    </view>
  </view>
</template>

<script lang="ts">
import Vue from 'vue'
import store from "@/store";

/**
 * 一天日程竖线排列组件
 *
 * @description 一天日程竖线排列组件，没有编辑功能
 * @property {String} day 显示事件的日期，格式yyyy-MM-dd
 */

export default Vue.extend({
  name: "EventScheduleVertical",
  props: {
    day: {
      type: String,
      required: true
    }
  },
  computed: {
    dayEvents(): Event[] {
      return store.getters.getDayEvents(this.day)
    }
  },
  methods: {
    x(time: string): number {
      return this.percent(time) * (365 - 32)
    },
    percent(time: string): number {
      const [hh, mm, ss] = time.split(':') as unknown as [number, number, number]
      return (hh * 3600 + mm * 60 + (ss << 0)) / (3600 * 24)
    },
  }
})
</script>

<style scoped>
.event-item {
  display: flex;
  width: 32px;
  height: 110px;
  flex-direction: column;
  align-items: center;
  position: absolute;
}

.vertical-line {
  border-width: 1px;
  width: 1px;
  border-style: solid;
}

.event-text {
  width: 15px;
  font-size: 12px;
  text-align: center;
  line-height: 15px;
  writing-mode: vertical-lr;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
</style>
