<template>
  <view>
    <!--    如果显示详情，顶部为-->
    <view class="top-container" v-if="showDetail">
      <view class="top-item">
        <picker :disabled="!editable" mode="time" :value="time" @change="timeChange">
          <text :style="{color:color,fontSize:fontSize+'px'}">{{ time }}</text>
        </picker>
      </view>
      <view class="top-item" :style="{height:fontSize+'px'}" style="position: relative">
        <input style="position: absolute" @input="titleInput" :disabled="!editable"
               :style="{...inputStyle,width:titleWidth}"
               :class="{'input-active':editable}"
               :value="title"/>
      </view>
      <view class="top-item">
        <picker v-if="editable" :range="repeatArray" @change="repeatChange">
          <uni-icons :color="color" :size="fontSize" type="plus"></uni-icons>
        </picker>
      </view>
    </view>
    <!--    不显示详情，顶部为-->
    <view v-else class="top-container">
      <picker :disabled="!editable" mode="time" :value="time" @change="timeChange">
        <text :style="{color:color,fontSize:fontSize+'px'}">{{ time }}</text>
      </picker>
      <picker v-if="editable" :range="repeatArray" @change="repeatChange">
        <uni-icons :color="color" :size="fontSize" type="plus"></uni-icons>
      </picker>
    </view>
    <!--          -->
    <view class="center-content">
      <picker v-if="showDetail" mode="multiSelector" style="width: 12px" :disabled="!editable" :range="alarmArray"
              @change="alarmChange">
        <view class="line" :style="lineStyle"></view>
      </picker>
      <view class="line" :style="lineStyle"></view>
      <picker v-if="showDetail" style="width: 12px;" :disabled="!editable" :range="colorArray" range-key="name"
              @change="colorChange">
        <view class="line" :style="lineStyle"></view>
      </picker>
    </view>
    <view class="bottom-content">
      <input @input="e=> { if(showDetail)  detailInput(e) ;else titleInput(e)}"
             :disabled="!editable" :value="showDetail?detail:title"
             :style="{...inputStyle,width:showDetail?detailWidth:titleWidth}"
             :class="{'input-active':editable}"/>
    </view>
  </view>
</template>

<script lang="ts">
import Vue from "vue";
import EventDaySchedule from '@/components/event-components/EventDaySchedule.vue'
import {colorArray} from "@/common/data";

/**
 * 事件轴线组件，EventDaySchedule的子组件
 *
 * @property {String} title 事件标题
 * @property {String} time 事件时间，格式HH:mm:ss
 * @property {String} detail 事件详情
 * @property {String} color 组件颜色
 * @property {String} alarm 提醒方式及时间，以*开头为闹钟提醒，后面数字为提前提醒的分钟数
 * @property {Boolean} showDetail = [true | false] 是否显示为详细模式
 * @property {Boolean} editable = [true | false] 是否开启编辑模式
 * @property {Number} fontSize 文字及图标大小
 * @event title-input 编辑title的事件，e:string=title
 * @event detail-input 编辑detail的事件，e:string=detail
 * @event color-picker-change 颜色选择器选择事件，e:string=color
 * @event time-picker-change 事件选择器选择事件，e:string=time
 * @event alarm-picker-change 提醒选择器选择事件，e:string=alarm
 * @event repeat-picker-change 重复选择器选择事件，e:number=[ 0 | 1 | 2 | 3 ]
 * @see EventDaySchedule
 */


export default Vue.extend({
  name: "EventLine",
  components: {},
  props: {
    time: {
      type: String,
      default: '00:00:00'
    },
    title: {
      type: String,
      default: ''
    },
    detail: {
      type: String,
      default: ''
    },
    color: {
      type: String,
      default: '#000000'
    },
    alarm: {
      type: String,
      default: '0'
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
      colorArray: colorArray,
      repeatArray: ['删除此事件', '删除此后的重复', '每天重复，100次', '每周重复，20次']
    };
  },
  computed: {
    alarmArray(): [string[], string[]] {
      const array: [string[], string[]] = [[], []]
      for (let i = 0; i < 31; i++) {
        array[1].push('提前' + i * 2 + '分钟')
      }
      array[0].push('通知提醒', '闹钟提醒')
      return array
    },
    lineStyle(): {
      borderColor: string,
      backgroundColor: string
    } {
      return {
        borderColor: this.color,
        backgroundColor: this.alarm.match(/^\*/) ? this.color : ''
      }
    },
    inputStyle(): {
      borderColor: string,
      color: string,
      fontSize: string,
      height: string,
    } {
      return {
        borderColor: this.color,
        color: this.color,
        fontSize: this.fontSize + 'px',
        height: this.fontSize + 'px',
      }
    },
    titleWidth(): string {
      return this.computedLen(this.title)
    },
    detailWidth(): string {
      return this.computedLen(this.detail)
    }
  },
  methods: {
    computedLen(text: string): string {
      const array = text.split('\n')
      let ml = array[0].replace(/[\uff00-\uffff\u4e00-\u9fa5]/g, "aa").length
      array.forEach((e: string) => {
        const l = e.replace(/[\uff00-\uffff\u4e00-\u9fa5]/g, "aa").length
        if (l > ml) ml = l
      })
      return (ml) * this.fontSize / 2 + 4 + 'px'
    },
    titleInput(e: any): void {
      this.$emit('title-input', e.detail.value as string)
    },
    detailInput(e: any): void {
      this.$emit('detail-input', e.detail.value as string)
    },
    colorChange(e: any): void {
      this.$emit('color-picker-change', this.colorArray[e.detail.value].value)
    },
    timeChange(e: any): void {
      this.$emit('time-picker-change', e.target.value + ':00')
    },
    alarmChange(e: any) {
      this.$emit('alarm-picker-change', (e.detail.value[0] === 0 ? '' : '*') + e.detail.value[1] * 2)
    },
    repeatChange(e: any) {
      this.$emit('repeat-picker-change', e.detail.value as number)
    }
  }
})
</script>

<style scoped>
.top-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 18px;
}

.top-item {
  width: 33%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.center-content {
  height: 2px;
  display: flex;
  align-items: center;
}

.line {
  flex: 1;
  border-width: 1px;
  height: 1px;
  border-style: solid;
}

.bottom-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 18px;
}

.input {
  text-align: center;
}

.input-active {
  text-align: center;
  min-width: 24px;
  border-width: 1px;
  border-style: dotted;
}
</style>
