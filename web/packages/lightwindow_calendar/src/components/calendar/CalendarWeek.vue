<template>
  <view>
    <view class="week-content">
      <view class="container-arrows">
        <view @click="previousWeek" class="arrow-wrapper">
          <view class="up-arrow"></view>
        </view>
        <view @click="nextWeek" class="arrow-wrapper">
          <view class="down-arrow"></view>
        </view>
      </view>
      <text
        @click="dayEventsClick(index)()"
        class="week-item"
        v-for="(item,index) in weekDateText"
        :key="index"
        :style="nowIndex ===index? activeStyle.style : ''"
      >{{ item }}</text>
    </view>
    <view class="container-schedule">
      <time-line class="-time-line"></time-line>
      <view class="container-events">
        <event-day-schedule
          @click.native="dayEventsClick(index)()"
          ref="weekSchedule"
          :key="index"
          v-for="(item,index) in week"
          :areaStyle="{ flex: 1 }"
          :showDetail="false"
          @plus-click="plusClick(index)()"
          :editable="editable"
          :fontSize="12"
          :day="day(index)"
        ></event-day-schedule>
      </view>
    </view>
  </view>
</template>

<script lang="ts">
import store from '../../store'
import TheBuildButton from "@/components/the-build-button/TheBuildButton.vue";
import TimeLine from '@/components/event-components/TimeLine.vue';
import {dayFormat, getEventDate} from "@/common/util";
import Vue from 'vue'
import EventDaySchedule from "@/components/event-components/EventDaySchedule.vue";

/**
 * *页面直接组件-日程-周
 *
 * @property {Boolean} editable = [true | false] 是否开启编辑模式
 * @see EventDaySchedule
 */

export default Vue.extend({
  components: {
    EventDaySchedule,
    TheBuildButton,
    TimeLine
  },
  name: "CalendarWeek",
  props: {
    editable: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      isBuildMode: false,
      week: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      activeStyle: {
        index: 8,
        style: {
          backgroundColor: '#007aff',
          color: '#ffffff'
        },
      },
    }
  },
  computed: {
    /**
     * 获取一周的Date对象
     * */
    weekDate(): Date[] {
      const mWeekDate: Date[] = []
      const e = getEventDate(this.activeDay)
      this.activeStyle.index = (e.getDay() + 6) % 7
      for (let i = 0; i < 7; i++) {
        const d = new Date()
        d.setTime(e.getTime() - 24 * 3600 * 1000 * (this.activeStyle.index - i))
        mWeekDate[i] = d
      }
      return mWeekDate
    },
    /**
     * 返回函数方便注入7天的day
     * */
    day(): (index: number) => string {
      return (index) => dayFormat(this.weekDate[index])
    },
    activeDay(): string {
      return store.state.activeDay
    },
    weekDateText(): string[] {
      const texts = []
      for (let i = 0; i < 7; i++) {
        const [, mm, dd] = dayFormat(this.weekDate[i]).split('-')
        texts[i] = mm + '-' + dd + '\n' + this.week[i]
      }
      return texts
    },
    nowIndex(): number {
      const d = new Date()
      const _nowIndex = (d.getDay() + 6) % 7
      if (dayFormat(this.weekDate[_nowIndex]) === dayFormat(d)) return _nowIndex
      else return -1
    },
  },
  methods: {
    plusClick(index: number): () => void {
      return () => {
        (this.$refs.weekSchedule as any[])[index].add()
      }
    },
    /**
     * 点击每个{@link EventDaySchedule}组件都会将{@link activeDay}切换到该日
     * */
    dayEventsClick(index: number): () => void {
      return () => {
        store.commit("setActiveDay", dayFormat(this.weekDate[index]))
        // console.log(store.state.activeDay)
      }
    },
    /**
     * 翻到下一周
     * */
    nextWeek() {
      const date = getEventDate(this.activeDay)
      date.setDate(date.getDate() + 7)
      store.commit('setActiveDay', dayFormat(date))
    },
    /**
     * 翻到上一周
     * */
    previousWeek() {
      const date = getEventDate(this.activeDay)
      date.setDate(date.getDate() - 7)
      store.commit('setActiveDay', dayFormat(date))
    }
  }
})
</script>

<style scoped>
.week-content {
  display: flex;
  align-items: center;
  justify-content: space-around;
  height: 50px;
  border-bottom-color: #e5e5e5;
  border-bottom-style: solid;
  border-bottom-width: 1px;
}

.week-item {
  flex-grow: 1;
  text-align: center;
  font-size: 12px;
  padding: 5px 0 5px 0;
}

.container-schedule {
  display: flex;
}

.container-events {
  display: flex;
  flex-grow: 1;
  justify-content: space-around;
  margin-left: -20px;
}

.container-arrows {
  box-sizing: border-box;
  height: 100%;
  padding: 5px 0;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  align-items: center;
}

.arrow-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 25px;
}

.up-arrow {
  transform: rotate(45deg);
  width: 10px;
  height: 10px;
  border-left: #808080 solid 2px;
  border-top: #555555 solid 2px;
}

.down-arrow {
  transform: rotate(225deg);
  width: 10px;
  height: 10px;
  border-left: #808080 solid 2px;
  border-top: #555555 solid 2px;
}
.-time-line {
  margin: 22px 0 22px 8px;
}
</style>
