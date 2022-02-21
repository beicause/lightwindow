<template>
  <view>
    <view v-show="!off" class="iframe" style="color: #007aff;display: flex;justify-content: center;align-items: center">
      <i class="fas fa-spinner fa-spin"></i>
    </view>
    <!--    采用iframe，直接使用div和echarts存在图标拖动无效的问题-->
    <iframe v-show="off" ref="iframe" class="iframe" src="/calendar/hybrid/html/graph.html"></iframe>
  </view>
</template>

<script lang="ts">
import Vue from 'vue'
import store from "@/store";
import {Event} from "@/common/data";
import {dayFormat, getEventDate, timeToSecond} from "@/common/util";

export default Vue.extend({
  name: "TheUserDataGraph",
  data() {
    return {
      off: null as null | (() => void)
    }
  },
  computed: {
    days(): string[] {
      const eventDates = new Set<number>()
      store.state.events.forEach(e => eventDates.add(getEventDate(e).getTime()))
      const minDate = Math.min(...eventDates)
      const maxDate = Math.max(...eventDates)
      const days = [] as string[]
      for (let i = minDate; i <= maxDate; i += 24 * 3600 * 1000) days.push(dayFormat(new Date(i)).replace(/-/, '\n'))
      return days
    },
    dayEvents(): Map<string, Event[]> {
      const dayEvents = new Map<string, Event[]>()
      this.days.forEach(d => {
        const day = d.replace('\n', '-')
        dayEvents.set(day, store.getters.getDayEvents(day))
      })
      return dayEvents
    },
    series(): any[] {
      const series = [] as any[]
      //先找出一天事件的最大数
      let num = 0
      this.dayEvents.forEach((value, key) => {
        if (key.length > num) num = key.length
      })
      //生成每个系列
      for (let i = 0; i < num; i++) {
        const data = [] as any[]//系列的数据
        this.dayEvents.forEach((value) => {
          const event = value[i]
          if (i < value.length) {
            data.push({
              name: event.time.substring(0, 5),
              value: timeToSecond(event.time) - (i === 0 ? 0 : timeToSecond(value[i - 1].time)),
              itemStyle: {
                borderColor: event.color
              },
              label: {
                color: event.color,
                formatter: '{b}'//不能是函数，否则JSON.stringify无效
              }
            })
          } else data.push(null)
        })
        series.push(
            {
              type: 'bar',
              stack: 's',
              itemStyle: {
                color: '#00000000'
              },
              label: {
                show: true,
                position: 'right',
                offset: [-4, 0],
                fontSize: 8,
              },
              data
            }
        )
      }
      return series
    },
    option(): any {
      return {
        title: {
          text: "事件分布：",
          textStyle: {
            fontWeight: 'normal',
            fontSize: 14
          },
          left: 15,
        },
        grid: {
          top: 30,
          bottom: 15
        },
        dataZoom: [
          {
            type: 'inside',
            orient: 'vertical',
            startValue: 0,
            endValue: 13,
          }
        ],
        yAxis: {
          type: 'category',
          inverse: true,
          data: (() => {
            const data = [] as any[]
            this.days.forEach(d => {
              data.push({
                value: d,
                textStyle: {
                  fontSize: 8
                }
              })
            })
            return data
          })()
        },
        xAxis: {
          type: 'value',
          show: false,
          min: 0,
          max: 24 * 3600
        },
        series: this.series
      }
    }
  },
  beforeMount() {
    //监听graph.html加载完成，否则立刻发送消息接收不到
    window.addEventListener('message', () => {
      const dom = this.$refs.iframe as any
      if (!dom)return;
      dom.contentWindow.postMessage(JSON.stringify(this.option), "*")
      this.off = store.watch((state) => state.events, () => {
        dom.contentWindow.postMessage(JSON.stringify(this.option), "*")
      })
    })

  },
  beforeDestroy() {
    if (this.off) this.off()
  }
})
</script>

<style scoped>
.iframe {
  height: 360px;
  width: 310px;
  border: none;
}
</style>
