<template>
  <movable-area style="height: 442px" :style="areaStyle">
    <movable-view v-for="(item,index) in mDayEvents" :y="timeToSecond(item.time) * 400 / (3600 * 24)" :key="index" :disabled="!editable"
                  direction="vertical" style="width: 100%;height: 42px;"
                  @change="e=> {
				    if (e.detail.source==='touch')changeTime(e.detail.y,item)
      }">
      <event-line @alarm-picker-change="e=>{item.alarm=e}"
                  @time-picker-change="e=>{item.time=e}"
                  @color-picker-change="e=>{item.color=e}"
                  @title-input="e=>{item.title=e}"
                  @detail-input="e=>{item.detail=e}"
                  @repeat-picker-change="e=>{repeatChange(item)(e)}"
                  :showDetail="showDetail" :color="item.color"
                  :fontSize="fontSize" :editable="editable"
                  :time="item.time.substr(0,5)" :detail="item.detail" :title="item.title"
                  :alarm="item.alarm"></event-line>
    </movable-view>
    <uni-transition :show="editable" :mode-class="['fade','slide-right']"
                    :styles="{position: 'absolute',bottom:-fontSize*1.5+'px',left:'calc(50% - '+fontSize*0.75+'px)'}">
      <uni-icons @click="$emit('plus-click')"
                 color="#007aff" :size="fontSize*1.5" type="plus"></uni-icons>
    </uni-transition>
  </movable-area>
</template>

<script lang="ts">
import store from '../../store'
import {Event} from "@/common/data";
import EventLine from "@/components/event-components/EventLine.vue"
import Vue from "vue";
import {compareEvents, dayFormat, getEventDate, getEventDiff, timeToSecond} from "@/common/util";

/**
 * 一天日程组件
 *
 * @property {Object} areaStyle 拖动区域movable-area的样式，movable-area需指定大小
 * @property {String} day 显示事件的日期，格式yyyy-MM-dd
 * @property {Boolean} showDetail = [true | false] 是否显示为详细模式
 * @property {Boolean} editable = [true | false] 是否开启编辑模式
 * @property {Number} fontSize 文字及图标大小
 * @link add 添加事件方法
 * @see EventLine
 */

export default Vue.extend({
  components: {
    EventLine
  },
  name: "EventDaySchedule",
  props: {
    areaStyle: {
      type: Object,
      default() {
        return {}
      }
    },
    day: {
      type: String,
      required: true
    },
    editable: {
      type: Boolean,
      default: false
    },
    showDetail: {
      type: Boolean,
      default: true
    },
    fontSize: {
      type: Number,
      default: 12
    },
  },
  data() {
    return {
      mDayEvents: [] as Event[],
      off: () => {}
    }
  },
  created() {
    this.off = store.watch((state) => state.events, () => {
      const dayEvents= JSON.parse(JSON.stringify(store.getters.getDayEvents(this.day)))
      const [me,e]=compareEvents(this.mDayEvents,dayEvents)
      e.forEach(event=>this.mDayEvents.push(event))
      me.forEach(e=>this.mDayEvents.splice(this.mDayEvents.indexOf(e),1))
      // console.log(this.mDayEvents)
    })
  },
  beforeDestroy() {
    this.off()
  },
  watch: {
    day: {
      immediate: true,
      handler(newValue: string): void {
        this.mDayEvents = JSON.parse(JSON.stringify(store.getters.getDayEvents(newValue)))
      }
    },
    mDayEvents: {
      immediate: true,
      deep: true,
      handler(newValue: Event[]) {
        const [ns, os] = compareEvents(newValue, store.getters.getDayEvents(this.day))
        if (ns.length === 0 && os.length === 0) return
        store.commit('updateDayEvents', {dayEvents: newValue, day: this.day})
      }
    }
  },
  methods: {
    timeToSecond(time:string):number{
      return timeToSecond(time)
    },
    changeTime(y: number, item: Event) {
      const percent = y / 400
      const secondAll = percent === 1 ? (24 * 3600 - 1) : 24 * 3600 * percent
      const hour = Math.floor(secondAll / 3600)
      const min = Math.floor((secondAll - hour * 3600) / 60)
      const second = Math.floor(secondAll - hour * 3600 - min * 60)
      item.time = (hour / 10 < 1 ? ('0' + hour) : hour) + ':'
          + (min / 10 < 1 ? ('0' + min) : min) + ':' + (second / 10 < 1 ? ('0' + second) : second)
    },
    repeatChange(item: Event) {
      return (e: number) => {
        switch (e) {
          case 0:
            store.commit('removeEvents', [item])
            break
          case 1:
            const repeatEvents: Event[] = []
            store.state.events.forEach(event => {
              const diffE = getEventDiff(event, item)
              if (Object.keys(diffE).length === 1 && typeof diffE.day === 'string' &&
                  getEventDate(diffE.day).getTime() > getEventDate(item.day).getTime()) {
                repeatEvents.push(event)
              }
            })
            store.commit('removeEvents', repeatEvents)
            break
          case 2:
            const newEvents: Event[] = []
            for (let i = 1; i < 100 + 1; i++) {
              const ne = JSON.parse(JSON.stringify(item))
              const date = getEventDate(item)
              date.setDate(date.getDate() + i)
              ne.day = dayFormat(date)
              newEvents.push(ne)
            }
            store.commit('addEvents', newEvents)
            break
          case 3:
            const newEvents1: Event[] = []
            for (let i = 1; i < 20 + 1; i++) {
              const ne = JSON.parse(JSON.stringify(item))
              const date = getEventDate(item)
              date.setDate(date.getDate() + i * 7)
              ne.day = dayFormat(date)
              newEvents1.push(ne)
            }
            store.commit('addEvents', newEvents1)
            break
        }
      }
    },

    /**
     * 添加事件，这里不调用，抛给父组件
     * */
    add(): void {
      this.mDayEvents.push({
        title: '',
        detail: '',
        time: '00:00:00',
        day: this.day,
        color: '#000000',
        alarm: '0'
      } as Event)
    }
  }
})
</script>

<style scoped>
</style>
